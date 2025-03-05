-- Inserting sample data into the accounts table
INSERT INTO accounts (user_id, account_number, account_type, balance)
VALUES (1, 'ACC100001', 'checking', 1000),
       (1, 'ACC100002', 'savings', 500),
       (2, 'ACC100003', 'checking', 1500),
       (2, 'ACC100004', 'savings', 2000),
       (3, 'ACC100005', 'checking', 0);

-- Inserting sample data into the transactions table
INSERT INTO transactions (account_id, idempotency_key, type, amount, timestamp, status)
VALUES (1, '550e8400-e29b-41d4-a716-446655440000', 'DEPOSIT', 500, CURRENT_TIMESTAMP, 'COMPLETED'),
       (1, '550e8400-e29b-41d4-a716-446655440001', 'WITHDRAWAL', 200, CURRENT_TIMESTAMP, 'PENDING'),
       (2, '550e8400-e29b-41d4-a716-446655440002', 'DEPOSIT', 300, CURRENT_TIMESTAMP, 'FAILED'),
       (2, '550e8400-e29b-41d4-a716-446655440003', 'WITHDRAWAL', 150, CURRENT_TIMESTAMP, 'COMPLETED'),
       (2, '550e8400-e29b-41d4-a716-446655440004', 'DEPOSIT', 700, CURRENT_TIMESTAMP, 'PENDING');
