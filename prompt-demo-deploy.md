Você é um engenheiro backend sênior especialista em Spring Boot. Vou publicar este projeto como DEMO pública, consumida diretamente por um site de portfólio em Angular, no free tier do Render (Docker, 512 MB de RAM, CPU muito limitada, hiberna após 15 min sem tráfego). Adapte o projeto SEM quebrar o comportamento atual: o perfil padrão e os testes existentes devem continuar funcionando. Tudo que for específico da demo fica no perfil Spring `demo`.

## REGRAS GERAIS
- Antes de alterar, leia o projeto e resuma em poucas linhas o que encontrou (versão do Spring Boot, segurança, banco, migrations, tratamento de erros)
- Mudanças mínimas e isoladas; não refatore o que não for necessário
- Sem dependências novas, exceto as indicadas ou indispensáveis (justifique)
- Segredos e origens sempre por variável de ambiente, nunca hardcoded
- Ao final, rode os testes e liste o que foi alterado

## TAREFAS

1. Perfil `demo` (application-demo.yml)
    - H2 em memória: jdbc:h2:mem:demo;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1 (se o projeto já usa H2, apenas adaptar)
    - spring.jpa.open-in-view=false, logs em INFO, sem SQL no log
    - server.port=${PORT:8080}
    - server.forward-headers-strategy=framework (atrás do proxy do Render: IP real do cliente e URLs https corretas, inclusive no Swagger)

2. Seed (somente perfil demo)
    - ApplicationRunner com @Profile("demo") que popula dados realistas e determinísticos
    - Todas as datas relativas ao momento atual, nunca fixas

3. Reset agendado e teto de dados (somente perfil demo)
    - @Scheduled (intervalo configurável, padrão 2h) que limpa as tabelas e roda o seed de novo, de forma thread-safe e com log claro
    - Teto de registros por tabela (configurável, padrão 500): ao exceder, escritas retornam 429 em ProblemDetail ("limite da demo atingido")

4. Health check
    - GET /health → 200 {"status":"UP"}, sem tocar no banco, sem autenticação, resposta mínima

5. CORS
    - Origens permitidas via CORS_ALLOWED_ORIGINS (lista separada por vírgula); se ausente no perfil demo, não permitir nenhuma origem e logar WARN
    - Métodos GET, POST, PATCH, DELETE, OPTIONS (atenção: PATCH precisa estar liberado), headers Authorization e Content-Type, sem credentials
    - O preflight (OPTIONS) deve ser respondido antes do filtro de autenticação
    - Incluir um teste que cubra o preflight de um PATCH

6. Rate limiting e limite de payload
    - Filtro simples em memória, por IP: 60 req/min no geral (configurável); responde 429 com header Retry-After e corpo ProblemDetail
    - Limpeza periódica dos contadores (sem vazamento de memória)
    - Rejeitar body com mais de 10 KB com 413 em ProblemDetail
    - /health e OPTIONS ficam isentos

7. Dockerfile e render.yaml
    - Multi-stage: build com Maven + JDK 21, runtime com JRE 21 enxuto, usuário não-root, .dockerignore
    - ENV SPRING_PROFILES_ACTIVE=demo
    - JAVA_TOOL_OPTIONS: -XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Xss512k
    - render.yaml (Blueprint): web service, runtime docker, plan free, healthCheckPath /health, envVars (segredos com generateValue; CORS_ALLOWED_ORIGINS com sync: false)

8. README
    - Nova seção "Demo pública": variáveis de ambiente, como rodar localmente com SPRING_PROFILES_ACTIVE=demo, como fazer deploy no Render, aviso de que os dados são temporários e resetam periodicamente

## ESPECÍFICO DO PROJETO (mantenha apenas o bloco que se aplica)

[A] Sistema de Agendamento Clínico (Spring Boot 4, Java 21, PostgreSQL, Flyway, JWT)
- No perfil demo, desabilitar o Flyway e usar spring.jpa.hibernate.ddl-auto=create (as migrations continuam valendo nos outros perfis). Se as migrations rodarem sem problema no H2, pode mantê-las, mas justifique
- Seed: 3 pacientes, 4 consultas ACTIVE em dias futuros distintos, 1 consulta COMPLETED com pagamento PIX quitado e 1 pagamento pendente. Use horários fora de 09:00-15:30 (ex.: 07:00 e 17:30), pois os cenários do site usam horários dentro dessa faixa e não podem colidir com o seed. Criar também o usuário demo@portfolio.dev / demo1234, documentado no README para quem quiser testar pelo Swagger
- JWT: secret via variável JWT_SECRET (no perfil demo, falhar na subida se ausente)
- Liberar /health, /swagger-ui/** e /v3/api-docs/** no SecurityConfig
- Token inválido, expirado ou de usuário que sumiu após o reset deve resultar em 401 com ProblemDetail, nunca 500. Requisição sem token também deve devolver 401 (AuthenticationEntryPoint); informe qual status o projeto devolvia antes
- Rate limit mais restrito em /auth/register e /auth/login (10 req/min por IP)
- Corrigir os PATCH que retornam 201 Created para 200 OK (atualizar testes e a documentação OpenAPI)
