INSERT INTO transactions (id, currency, amount, date, createdAt, updatedAt) VALUES
(1, 'USD', 212.02, datetime("now", "-3 days"), datetime("now"), datetime("now")),
(2, 'CAD', 2932.2, datetime("now", "-2 days"), datetime("now"), datetime("now")),
(3, 'CAD', 10.24, datetime("now", "-2 days"), datetime("now"), datetime("now")),
(4, 'USD', 99.8, datetime("now", "-1 days"), datetime("now"), datetime("now")),
(5, 'CAD', 928.14, datetime("now", "localtime"), datetime("now"), datetime("now")),
(6, 'EUR', 2351.2, datetime("now", "-24 days"), datetime("now"), datetime("now")),
(7, 'CAD', 11.57, datetime("now", "-9 days"), datetime("now"), datetime("now")),
(8, 'CAD', 25, datetime("now", "-7 days"), datetime("now"), datetime("now")),
(9, 'EUR', 23.98, datetime("now", "-7 days"), datetime("now"), datetime("now")),
(10, 'USD', 88.6, datetime("now", "-33 days"), datetime("now"), datetime("now")),
(11, 'USD', 82.6, datetime("now", "-63 days"), datetime("now"), datetime("now")),
(12, 'USD', 18.6, datetime("now", "-59 days"), datetime("now"), datetime("now")),
(13, 'USD', 29.6, datetime("now", "-55 days"), datetime("now"), datetime("now")),
(14, 'USD', 43.6, datetime("now", "-717 days"), datetime("now"), datetime("now")),
(15, 'USD', 57.6, datetime("now", "-683 days"), datetime("now"), datetime("now")),
(16, 'USD', 10.6, datetime("now", "-679 days"), datetime("now"), datetime("now")),
('17', 'USD', 42.02, datetime("now", "-4 days"), datetime("now"), datetime("now")),
('18', 'USD', 10.0, datetime("now", "-4 days"), datetime("now"), datetime("now")),
('19', 'USD', 72.34, datetime("now", "-5 days"), datetime("now"), datetime("now")),
('20', 'USD', 10.0, datetime("now", "-487 days"), datetime("now"), datetime("now")),
('21', 'USD', 192.18, datetime("now", "-851 days"), datetime("now"), datetime("now")),
('22', 'USD', 98.99, datetime("now", "-1091 days"), datetime("now"), datetime("now")),
('23', 'USD', 28.13, datetime("now", "-1051 days"), datetime("now"), datetime("now")),
('24', 'USD', 287.19, datetime("now", "-1102 days"), datetime("now"), datetime("now")),
('25', 'USD', 193.44, datetime("now", "-1344 days"), datetime("now"), datetime("now")),
('26', 'USD', 13.44, datetime("now", "-1584 days"), datetime("now"), datetime("now")),
('27', 'USD', 44.13, datetime("now", "-1573 days"), datetime("now"), datetime("now")),
('28', 'USD', 1.32, datetime("now", "-1753 days"), datetime("now"), datetime("now")),
('29', 'USD', 828.13, datetime("now", "-1914 days"), datetime("now"), datetime("now")),
('30', 'USD', 600.25, datetime("now", "-2277 days"), datetime("now"), datetime("now")),
('31', 'CAD', 101.9, datetime("now", "-1560 days"), datetime("now"), datetime("now")),
('32', 'CAD', 21.9, datetime("now", "-1750 days"), datetime("now"), datetime("now")),
('33', 'CAD', 2000.9, datetime("now", "-1890 days"), datetime("now"), datetime("now")),
('34', 'CAD', 601.1, datetime("now", "-1970 days"), datetime("now"), datetime("now")),
('35', 'CAD', 64.23, datetime("now", "-2030 days"), datetime("now"), datetime("now"));
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (1, 'USD', 'CAD', 1.35);
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (2, 'CAD', 'USD', 0.74);
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (3, 'USD', 'EUR', 0.94);
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (4, 'EUR', 'USD', 1.07);
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (5, 'EUR', 'CAD', 1.45);
INSERT OR IGNORE INTO conversion (id, fromCurr, toCurr, rate) VALUES (6, 'CAD', 'EUR', 0.69);
