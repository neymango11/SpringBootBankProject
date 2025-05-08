-- Create the banking database if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'banking_db') THEN
        CREATE DATABASE banking_db;
    END IF;
END $$; 