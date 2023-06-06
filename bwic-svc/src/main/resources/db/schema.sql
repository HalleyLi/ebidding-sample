--create database if not exists ebidding_bwic;
--use ebidding_bwic;


CREATE TABLE IF NOT EXISTS bwic (
                                     id VARCHAR(255),
                                     client_id VARCHAR(255),
                                     cusip VARCHAR(255),
									 size DOUBLE,
                                     starting_price DOUBLE,
                                     version bigint,
                                     active boolean,
                                     due_date TIMESTAMP,
                                     PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bond_reference (
                                     id VARCHAR(255),
                                     cusip VARCHAR(255),
                                     issuer VARCHAR(255),
                                     rating VARCHAR(255),
                                     coupon VARCHAR(255),
                                     maturitydate VARCHAR(255),
                                     PRIMARY KEY (id)
) ENGINE=InnoDB;
