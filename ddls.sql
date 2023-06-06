drop database if exists ebidding_bid;

create database if not exists ebidding_bid;
use ebidding_bid;
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

DELIMITER $$
CREATE TRIGGER archive_bid AFTER UPDATE ON bid FOR EACH ROW
BEGIN
    IF OLD.price<> NEW.price THEN
    INSERT into bid_history VALUES (OLD.id, OLD.client_id, OLD.bwic_id, OLD.price, OLD.size, OLD.transaction_id, OLD._rank, OLD.feedback, OLD.version, OLD.effective_time, OLD.active);
	END IF;
END$$

DELIMITER ;

insert into bid values('1', 'test-client', '1', 100.0, 10.0, '', 1, '', 0, current_timestamp, true);


drop database if exists ebidding_bwic;

create database if not exists ebidding_bwic;
use ebidding_bwic;


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

insert into bond_reference values(0, "310867UR2", "BAOSTEEL GROUP", "CC/Ca/CCC", "10.15%", "2026-09-06");
insert into bond_reference values(1, "453012O20", "Greenergy Fuels Holdings", "AA/Aaa/AA", "3.33%", "2025-09-17");
insert into bond_reference values(2, "270465DW9", "STATE BANK OF INDIA", "A/A/BBB", "4.17%", "2028-07-13");
insert into bond_reference values(3, "572816A98", "ACS", "AAA/Aaa/AAA", "3.28%", "2027-03-07");
insert into bond_reference values(4, "083597SD9", "DONGFENG MOTOR GROUP", "CCC/B/B", "7.20%", "2025-03-25");
insert into bond_reference values(5, "9385218R8", "THYSSENKRUPP", "C/Ca/CC", "10.09%", "2026-07-01");
insert into bond_reference values(6, "9283740E3", "ORACLE", "CC/C/CC", "11.68%", "2026-03-07");
insert into bond_reference values(7, "250931S57", "ROYAL BANK OF CANADA", "A/A/A", "3.56%", "2023-06-13");
insert into bond_reference values(8, "285190EU8", "ROYAL PHILIPS", "AAA/Aa/AAA", "3.32%", "2025-01-03");
insert into bond_reference values(9, "056842BE2", "HNA Group", "BB/Ba/BB", "6.75%", "2024-05-10");
insert into bond_reference values(10, "346087FV8", "CHINA MINSHENG BANKING", "CC/Ca/CC", "10.01%", "2025-02-20");
insert into bond_reference values(11, "386195TC5", "LUKOIL", "CCC/Caa/CCC", "10.77%", "2030-01-15");
insert into bond_reference values(12, "072643RZ2", "JX HOLDINGS", "BBB/Ba/BB", "6.06%", "2031-08-11");
insert into bond_reference values(13, "659382Y10", "Centene", "BB/Ba/BB", "6.34%", "2026-02-10");
insert into bond_reference values(14, "013569EM5", "LOCKHEED MARTIN", "C/C/D", "12.36%", "2023-07-23");
insert into bond_reference values(15, "591863SK9", "CPC", "BB/B/BB", "6.88%", "2028-01-30");
insert into bond_reference values(16, "537601T10", "ABB", "BB/Ba/BB", "6.44%", "2028-12-20");
insert into bond_reference values(17, "897620S41", "SODEXO", "AA/Aa/A", "3.45%", "2025-12-21");
insert into bond_reference values(18, "069458LK7", "ROYAL BANK OF SCOTLAND GROUP", "BBB/Baa/BBB", "7.19%", "2025-02-19");
insert into bond_reference values(19, "4325616E3", "WORLD FUEL SERVICES", "CCC/B/B", "7.72%", "2031-05-04");
insert into bond_reference values(20, "865917EJ2", "L'ORÉAL", "A/Baa/BBB", "5.98%", "2025-07-10");
insert into bond_reference values(21, "8905643D4", "DZ BANK", "C/Ca/C", "10.21%", "2027-03-25");
insert into bond_reference values(22, "071293682", "Shanxi Coking Coal Group", "NR/C/NR", "15.79%", "2023-10-16");
insert into bond_reference values(23, "105693949", "HINDUSTAN PETROLEUM", "AA/Aa/AA", "3.15%", "2026-03-27");
insert into bond_reference values(24, "0135947A6", "LIBERTY MUTUAL INSURANCE GROUP", "AA/Aa/A", "3.47%", "2031-03-28");
insert into bond_reference values(25, "685139MZ7", "ACCENTURE", "CCC/Caa/B", "8.52%", "2025-05-27");
insert into bond_reference values(26, "419827PT6", "JFE HOLDINGS", "B/Caa/B", "7.15%", "2027-02-24");
insert into bond_reference values(27, "486317XV2", "CARDINAL HEALTH", "BBB/Ba/BB", "7.07%", "2025-06-26");
insert into bond_reference values(28, "792108680", "LM Ericsson", "C/C/C", "10.43%", "2025-11-22");
insert into bond_reference values(29, "830471HS4", "CNP ASSURANCES", "BBB/A/BBB", "5.61%", "2030-07-19");
insert into bond_reference values(30, "7205146E5", "AISIN SEIKI", "A/A/AA", "3.33%", "2028-06-07");
insert into bond_reference values(31, "169247CJ2", "EMC", "D/C/NR", "14.25%", "2029-08-27");
insert into bond_reference values(32, "410672PC0", "SUMITOMO MITSUI FINANCIAL GROUP", "D/C/NR", "14.37%", "2029-11-28");
insert into bond_reference values(33, "921807844", "HCA HOLDINGS", "NR/C/NR", "15.25%", "2030-03-17");
insert into bond_reference values(34, "6738909K5", "ZF Friedrichshafen", "B/B/BB", "6.09%", "2027-03-21");
insert into bond_reference values(35, "591048DN2", "WILMAR INTERNATIONAL", "CC/Ca/CC", "9.11%", "2029-11-23");
insert into bond_reference values(36, "281049V21", "POWER CORP. OF CANADA", "CCC/B/CCC", "8.19%", "2031-04-27");
insert into bond_reference values(37, "802594861", "AMERICAN EXPRESS", "NR/C/NR", "16.33%", "2027-01-23");
insert into bond_reference values(38, "378460M48", "Finatis", "B/B/B", "6.25%", "2028-06-03");
insert into bond_reference values(39, "062487L34", "HON HAI PRECISION INDUSTRY", "AAA/Aa/AA", "3.40%", "2024-04-18");
insert into bond_reference values(40, "281965S89", "CENTRICA", "B/B/BB", "6.01%", "2031-09-03");
insert into bond_reference values(41, "916873LB1", "ALUMINUM CORP. OF CHINA", "BBB/A/BBB", "5.91%", "2024-03-24");
insert into bond_reference values(42, "216490233", "AUSTRALIA & NEW ZEALAND BANKING GROUP", "BBB/Baa/BBB", "6.90%", "2028-09-22");
insert into bond_reference values(43, "507241N74", "LLOYDS BANKING GROUP", "NR/C/NR", "14.16%", "2027-04-23");
insert into bond_reference values(44, "945617FN6", "MCDONALD'S", "CC/Caa/CC", "10.06%", "2030-04-10");
insert into bond_reference values(45, "8531722V3", "TRAVELERS COS.", "B/Ba/B", "6.20%", "2030-05-10");
insert into bond_reference values(46, "537601651", "ABB", "D/C/D", "12.90%", "2025-05-28");
insert into bond_reference values(47, "923657FL3", "SANOFI", "BBB/Ba/BBB", "7.15%", "2025-07-01");
insert into bond_reference values(48, "507342FE6", "Shanxi LuAn Mining Group", "BBB/A/A", "4.23%", "2031-02-27");
insert into bond_reference values(49, "182537MI5", "REPSOL", "BB/Ba/BBB", "6.37%", "2025-09-20");
insert into bond_reference values(50, "605481H59", "LYONDELLBASELL INDUSTRIES", "A/A/A", "3.21%", "2023-06-15");
insert into bond_reference values(51, "369240FM3", "JIANGSU SHAGANG GROUP", "BBB/Baa/BBB", "6.13%", "2026-04-24");
insert into bond_reference values(52, "4179655I6", "GROUPE BPCE", "CCC/Caa/CCC", "9.81%", "2026-03-30");
insert into bond_reference values(53, "425679104", "SAMSUNG ELECTRONICS", "D/C/D", "13.84%", "2027-10-17");
insert into bond_reference values(54, "068792AI2", "CHINA DATANG", "B/Ba/BB", "6.25%", "2026-12-17");
insert into bond_reference values(55, "278904PF0", "INTL FCSTONE", "AA/Aa/A", "3.18%", "2024-03-05");
insert into bond_reference values(56, "0698233V8", "BERKSHIRE HATHAWAY", "AA/Aaa/AAA", "3.57%", "2025-06-16");
insert into bond_reference values(57, "584091KD0", "MONDELEZ INTERNATIONAL", "AAA/Aa/AA", "3.24%", "2028-04-05");
insert into bond_reference values(58, "273490HW2", "Louis Dreyfus", "AA/Aa/A", "3.25%", "2026-06-13");
insert into bond_reference values(59, "465071VG5", "CHINA SOUTH INDUSTRIES GROUP", "BB/B/BB", "6.12%", "2031-11-15");
insert into bond_reference values(60, "029163U74", "MORGAN STANLEY", "D/C/D", "13.80%", "2028-08-17");
insert into bond_reference values(61, "362087E04", "ADECCO GROUP", "D/C/D", "13.76%", "2025-12-26");
insert into bond_reference values(62, "0135694V7", "LOCKHEED MARTIN", "B/B/B", "6.98%", "2026-11-09");
insert into bond_reference values(63, "9427832Y4", "RIO TINTO GROUP", "A/Aa/A", "3.22%", "2028-12-21");
insert into bond_reference values(64, "923657CU4", "SANOFI", "B/B/B", "7.12%", "2027-07-15");
insert into bond_reference values(65, "083597XM4", "DONGFENG MOTOR GROUP", "BB/B/BB", "6.39%", "2029-02-06");
insert into bond_reference values(66, "4253198V0", "RWE", "BB/Ba/BBB", "7.19%", "2025-11-11");
insert into bond_reference values(67, "653207BS1", "CHINA NATIONAL OFFSHORE OIL", "B/Caa/CCC", "8.23%", "2031-07-22");
insert into bond_reference values(68, "345801MR7", "SSE", "AA/Aa/A", "3.24%", "2028-08-03");
insert into bond_reference values(69, "057423WS4", "APPLE", "D/C/NR", "15.20%", "2032-05-30");
insert into bond_reference values(70, "031568907", "METRO", "BB/Baa/BB", "6.42%", "2032-05-17");
insert into bond_reference values(71, "849053MU7", "CATERPILLAR", "BB/Baa/BBB", "7.16%", "2029-12-02");
insert into bond_reference values(72, "973821LD5", "SAP", "BB/Ba/B", "6.79%", "2023-06-26");
insert into bond_reference values(73, "230978AS3", "CHINA TELECOMMUNICATIONS", "NR/C/NR", "14.09%", "2025-12-18");
insert into bond_reference values(74, "185640FN1", "MITSUBISHI", "AAA/Aaa/AA", "3.44%", "2029-12-14");
insert into bond_reference values(75, "673890LN7", "ZF Friedrichshafen", "NR/C/D", "13.76%", "2026-03-29");
insert into bond_reference values(76, "921807ZY4", "HCA HOLDINGS", "A/A/AA", "3.45%", "2024-12-28");
insert into bond_reference values(77, "768501H05", "DEERE", "C/C/D", "12.13%", "2023-11-24");
insert into bond_reference values(78, "870132HC4", "DEUTSCHE TELEKOM", "NR/C/D", "13.41%", "2028-07-29");
insert into bond_reference values(79, "1450360G6", "SCHNEIDER ELECTRIC", "C/C/CC", "11.35%", "2031-04-05");
insert into bond_reference values(80, "5873462U0", "SCHLUMBERGER", "B/B/BB", "6.36%", "2025-03-27");
insert into bond_reference values(81, "964873RP6", "TATA MOTORS", "D/C/NR", "14.33%", "2023-09-25");
insert into bond_reference values(82, "3427893X9", "IDEMITSU KOSAN", "NR/C/NR", "16.49%", "2024-06-04");
insert into bond_reference values(83, "963275GF2", "SEVEN & I HOLDINGS", "C/C/C", "11.07%", "2024-03-28");
insert into bond_reference values(84, "074962HZ3", "Taiwan Semiconductor Manufacturing", "A/Baa/A", "4.20%", "2027-11-29");
insert into bond_reference values(85, "297405HF9", "SUNCOR ENERGY", "NR/C/D", "13.57%", "2024-10-28");
insert into bond_reference values(86, "083597XZ0", "DONGFENG MOTOR GROUP", "A/Aa/AA", "3.25%", "2031-02-07");
insert into bond_reference values(87, "913467GH5", "Rolls-Royce Holdings", "D/C/D", "12.69%", "2027-01-21");
insert into bond_reference values(88, "9013828F8", "CHINA LIFE INSURANCE", "BBB/Ba/BB", "6.33%", "2029-05-25");
insert into bond_reference values(89, "590618DN2", "TOSHIBA", "A/Aa/AA", "3.30%", "2026-10-11");
insert into bond_reference values(90, "904532FW7", "VERIZON COMMUNICATIONS", "NR/C/NR", "16.76%", "2030-01-29");
insert into bond_reference values(91, "0624872S9", "HON HAI PRECISION INDUSTRY", "BB/Ba/BBB", "6.39%", "2027-11-14");
insert into bond_reference values(92, "269753T03", "INTEL", "CC/Caa/CC", "10.67%", "2025-12-29");
insert into bond_reference values(93, "314927HL2", "WELLS FARGO", "BBB/Ba/BB", "6.71%", "2027-10-18");
insert into bond_reference values(94, "689710KE9", "Phillips 66", "A/A/A", "3.47%", "2028-01-10");
insert into bond_reference values(95, "0639254O1", "QUANTA COMPUTER", "NR/C/NR", "15.13%", "2029-07-31");
insert into bond_reference values(96, "017284IR3", "MEDIPAL HOLDINGS", "D/C/C", "13.10%", "2024-10-11");
insert into bond_reference values(97, "923618FE5", "LOWE'S", "BB/Ba/BB", "6.98%", "2026-09-25");
insert into bond_reference values(98, "013569BN8", "LOCKHEED MARTIN", "A/Baa/A", "4.06%", "2023-09-22");
insert into bwic values('1', 'CITI', '923618FE5', 10, 100.0, 0, true, date_add(current_timestamp, interval 1 day));
insert into bwic values('2', 'AAPL', '013569BN8', 20, 99.0, 0, true, date_add(current_timestamp, interval 1 day));
insert into bwic values('3', 'BABA', '0639254O1', 30, 109.0, 0, true, date_add(current_timestamp, interval 1 day));
insert into bwic values('4', 'IBM', '0624872S9', 40, 106.0, 0, true, date_add(current_timestamp, interval 1 day));
insert into bwic values('5', 'TOYOTA', '083597XZ0', 50, 89.0, 0, true, date_add(current_timestamp, interval 1 day));



drop database if exists ebidding_account;
create database ebidding_account;
use ebidding_account;

CREATE TABLE IF NOT EXISTS account (
                                     id VARCHAR(255),
                                     name VARCHAR(255) NOT NULL default '',
                                     member_since TIMESTAMP NOT NULL default current_timestamp,
                                     role VARCHAR(255) NOT NULL default '',
                                     password_hash VARCHAR(100) default '',
                                     PRIMARY KEY (id)
) ENGINE=InnoDB;
insert into account values('1', 'test-trader', current_timestamp, 'TRADER', 'b8ad08a3a547e35829b821b75370301dd8c4b06bdd7771f9b541a75914068718');
insert into account values('2', 'test-client', current_timestamp, 'CLIENT', 'b8ad08a3a547e35829b821b75370301dd8c4b06bdd7771f9b541a75914068718');
insert into account values('3', 'test-client-A', current_timestamp, 'CLIENT', 'b8ad08a3a547e35829b821b75370301dd8c4b06bdd7771f9b541a75914068718');
insert into account values('4', 'test-client-B', current_timestamp, 'CLIENT', 'b8ad08a3a547e35829b821b75370301dd8c4b06bdd7771f9b541a75914068718');
insert into account values('5', 'test-client-C', current_timestamp, 'CLIENT', 'b8ad08a3a547e35829b821b75370301dd8c4b06bdd7771f9b541a75914068718');

