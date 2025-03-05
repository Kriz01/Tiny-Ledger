-- Inserting sample data into the accounts table
INSERT INTO accounts (user_id, account_number, account_type, balance)
VALUES (1, 'ACC100001', 'checking', 800),
    (1, 'ACC100002', 'savings', 600),
    (2, 'ACC100003', 'checking', 1500),
    (2, 'ACC100004', 'savings', 2000),
    (3, 'ACC100005', 'checking', 0);


INSERT INTO transactions (account_id, idempotency_key, type, amount, timestamp, status)
VALUES
    (1, '550e8400-e29b-41d4-a716-446655440000', 'DEPOSIT', 9000, '2024-02-26 08:00:00', 'COMPLETED'),    -- Oldest
    (1, '550e8400-e29b-41d4-a716-446655440001', 'WITHDRAWAL', 700, '2024-02-26 08:05:00', 'PENDING'),
    (1, '550e8400-e29b-41d4-a716-446655440008', 'WITHDRAWAL', 700, '2024-02-26 08:06:00', 'PENDING'),
    (2, '550e8400-e29b-41d4-a716-446655440002', 'DEPOSIT', 1500, '2024-02-26 08:10:00', 'FAILED'),
    (2, '550e8400-e29b-41d4-a716-446655440003', 'DEPOSIT', 1000, '2024-02-26 08:15:00', 'COMPLETED'),
    (3, '550e8400-e29b-41d4-a716-446655440004', 'WITHDRAWAL', 50, '2024-02-26 08:20:00', 'PENDING');    -- Newest
