CREATE SCHEMA minibank;

SET SCHEMA minibank;

CREATE TABLE account (
  id INT IDENTITY PRIMARY KEY,
  balance DECIMAL(11,2) NOT NULL DEFAULT '0.00',
  iban VARCHAR(24) NOT NULL,
  creation_date TIMESTAMP NOT NULL,
  cancellation_date TIMESTAMP NULL);

ALTER TABLE account ADD CONSTRAINT unique_iban UNIQUE (iban);

CREATE TABLE transaction (
  id INT IDENTITY PRIMARY KEY,
  reference VARCHAR(10) NOT NULL,
  amount DECIMAL(11,2) NOT NULL,
  fee DECIMAL(5,2) NOT NULL DEFAULT '0.00',
  description VARCHAR(50) NOT NULL DEFAULT 'MiniBank Transaction',
  transaction_date TIMESTAMP NOT NULL,
  origin_account_id INT NOT NULL,
  destination_account_id INT NOT NULL);

ALTER TABLE transaction ADD CONSTRAINT unique_reference UNIQUE (reference);
ALTER TABLE transaction ADD FOREIGN KEY (origin_account_id) REFERENCES account(id);
ALTER TABLE transaction ADD FOREIGN KEY (destination_account_id) REFERENCES account(id);
