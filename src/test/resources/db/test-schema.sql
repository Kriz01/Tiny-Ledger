DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
CREATE TABLE IF NOT EXISTS accounts (
                                        account_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        user_id BIGINT NOT NULL,
                                        account_number VARCHAR(20) UNIQUE NOT NULL,
                                        account_type VARCHAR(20) NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        balance INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS transactions (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            account_id BIGINT NOT NULL,
                                            idempotency_key UUID UNIQUE,  -- Ensures uniqueness per request
                                            type TEXT NOT NULL,
                                            amount INTEGER DEFAULT 0 NOT NULL,
                                            timestamp DATETIME NOT NULL,
                                            status VARCHAR(20) DEFAULT 'PENDING',  -- Can track PENDING, COMPLETED, FAILED
                                            FOREIGN KEY (account_id) REFERENCES accounts(account_id)
    );