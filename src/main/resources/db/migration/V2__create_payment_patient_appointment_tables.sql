CREATE TABLE patient
(
    id           VARCHAR(21) PRIMARY KEY NOT NULL UNIQUE,
    name         VARCHAR(200)            NOT NULL,
    phone_number VARCHAR(11)             NOT NULL,
    birthday     DATE                    NOT NULL,
    created_at   TIMESTAMPTZ             NOT NULL,
    updated_at   TIMESTAMPTZ             NOT NULL
);

CREATE TABLE appointment
(
    id                VARCHAR(21) PRIMARY KEY NOT NULL UNIQUE,
    title             VARCHAR(200)            NOT NULL,
    description       VARCHAR(1500),
    start_appointment TIMESTAMPTZ             NOT NULL,
    end_appointment   TIMESTAMPTZ             NOT NULL,
    situation         VARCHAR(20)             NOT NULL,
    patient_id        VARCHAR(21)             NOT NULL REFERENCES patient (id)
);

CREATE TABLE payment
(
    id             VARCHAR(21) PRIMARY KEY NOT NULL UNIQUE,
    value          DECIMAL                 NOT NULL,
    payment_type   VARCHAR(20),
    is_done        BOOLEAN                 NOT NULL,
    appointment_id VARCHAR(21)             NOT NULL REFERENCES appointment (id),
    created_at     TIMESTAMPTZ             NOT NULL,
    updated_at     TIMESTAMPTZ             NOT NULL
);