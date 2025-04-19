-- Clear existing data
TRUNCATE topup_methods, transfer_categories, transaction_types RESTART IDENTITY CASCADE;

-- Insert transaction types
INSERT INTO transaction_types (id, name) VALUES (1, 'transfer');
INSERT INTO transaction_types (id, name) VALUES (2, 'topup');

-- Insert transfer categories
INSERT INTO transfer_categories (id, name) VALUES (1, 'needs');
INSERT INTO transfer_categories (id, name) VALUES (2, 'bills');
INSERT INTO transfer_categories (id, name) VALUES (3, 'shopping');
INSERT INTO transfer_categories (id, name) VALUES (4, 'transport');
INSERT INTO transfer_categories (id, name) VALUES (5, 'transfer_of_wealth');

-- Insert topup methods
INSERT INTO topup_methods (id, name) VALUES (1, 'bank_transfer');
INSERT INTO topup_methods (id, name) VALUES (2, 'debit_card');
INSERT INTO topup_methods (id, name) VALUES (3, 'credit_card');