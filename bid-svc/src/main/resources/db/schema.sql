--create database if not exists ebidding_bid;
--use ebidding_bid;

CREATE TABLE IF NOT EXISTS bid (
                                     id VARCHAR(255),
                                     client_id VARCHAR(255),
                                     bwic_id VARCHAR(255),
                                     price DOUBLE,
									 size DOUBLE,
                                     transaction_id VARCHAR(255),
                                     _rank bigint,
                                     feedback VARCHAR(255),
                                     version bigint,
                                     effective_time TIMESTAMP,
                                     active boolean,
                                     PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bid_history (
                                     id VARCHAR(255),
                                     client_id VARCHAR(255),
                                     bwic_id VARCHAR(255),
                                     price DOUBLE,
									 size DOUBLE,
                                     transaction_id VARCHAR(255),
                                     _rank bigint,
                                     feedback VARCHAR(255),
                                     version bigint,
                                     effective_time TIMESTAMP,
                                     active boolean,
                                     PRIMARY KEY (id, version)
) ENGINE=InnoDB;