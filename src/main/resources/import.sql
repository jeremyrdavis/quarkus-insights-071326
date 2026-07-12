-- This file allow to write SQL commands that will be emitted in test and dev.

-- Default CFP with reasonable defaults
insert into cfp (id, cfpOpens, cfpCloses, conferenceName, conferenceUrl, conferenceDescription, contactEmailAddress)
values ('11111111-1111-1111-1111-111111111111',
        '2026-01-01',
        '2026-03-31',
        'Quarkus Insights Conference',
        'https://quarkus.io/insights',
        'A community conference covering Quarkus, Java, cloud-native development, and modern software practices.',
        'cfp@quarkus.io');

-- Session formats for the default CFP (duration stored as nanoseconds)
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111111', 'TECHNICAL_SESSION', 'Technical Session', 'A standard conference talk on a technical topic.', 2700000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('22222222-2222-2222-2222-222222222202', '11111111-1111-1111-1111-111111111111', 'HANDS_ON_LAB', 'Hands-on Lab', 'An interactive workshop where attendees code along.', 7200000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('22222222-2222-2222-2222-222222222203', '11111111-1111-1111-1111-111111111111', 'KEYNOTE', 'Keynote', 'A featured presentation for all attendees.', 3600000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('22222222-2222-2222-2222-222222222204', '11111111-1111-1111-1111-111111111111', 'BYTE_SIZE', 'Byte Size', 'A short, focused talk on a single topic.', 900000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('22222222-2222-2222-2222-222222222205', '11111111-1111-1111-1111-111111111111', 'IGNITE', 'Ignite', 'A fast-paced talk with auto-advancing slides.', 300000000000);

-- Second CFP
insert into cfp (id, cfpOpens, cfpCloses, conferenceName, conferenceUrl, conferenceDescription, contactEmailAddress)
values ('44444444-4444-4444-4444-444444444444',
        '2026-07-01',
        '2026-09-30',
        'Java Dev Summit',
        'https://javadevsummit.io',
        'An annual gathering for Java developers covering the full ecosystem — from language internals to cloud-native production systems.',
        'cfp@javadevsummit.io');

-- Session formats for the second CFP (duration stored as nanoseconds)
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('55555555-5555-5555-5555-555555555501', '44444444-4444-4444-4444-444444444444', 'TECHNICAL_SESSION', 'Technical Session', 'A standard conference talk on a technical topic.', 2700000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('55555555-5555-5555-5555-555555555502', '44444444-4444-4444-4444-444444444444', 'HANDS_ON_LAB', 'Hands-on Lab', 'An interactive workshop where attendees code along.', 7200000000000);
insert into format (id, cfp_id, formatCode, title, description, duration)
values ('55555555-5555-5555-5555-555555555503', '44444444-4444-4444-4444-444444444444', 'BYTE_SIZE', 'Byte Size', 'A short, focused talk on a single topic.', 900000000000);

-- Tracks for the second CFP
insert into track (id, cfp_id, trackCode, title, description)
values ('66666666-6666-6666-6666-666666666601', '44444444-4444-4444-4444-444444444444', 'JAVA_LANGUAGE', 'Java Language', 'The Java language, new features, and the JVM.');
insert into track (id, cfp_id, trackCode, title, description)
values ('66666666-6666-6666-6666-666666666602', '44444444-4444-4444-4444-444444444444', 'SERVER_SIDE_JAVA', 'Server-Side Java', 'Frameworks and libraries for server-side Java development.');
insert into track (id, cfp_id, trackCode, title, description)
values ('66666666-6666-6666-6666-666666666603', '44444444-4444-4444-4444-444444444444', 'CLOUD', 'Cloud', 'Cloud-native development, Kubernetes, and serverless.');
insert into track (id, cfp_id, trackCode, title, description)
values ('66666666-6666-6666-6666-666666666604', '44444444-4444-4444-4444-444444444444', 'DEV_PRACTICES', 'Developer Practices', 'Testing, CI/CD, and modern development practices.');
insert into track (id, cfp_id, trackCode, title, description)
values ('66666666-6666-6666-6666-666666666605', '44444444-4444-4444-4444-444444444444', 'ARCHITECTURE', 'Architecture', 'Software architecture, DDD, and system design.');

-- Tracks for the default CFP
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', 'JAVA_LANGUAGE', 'Java Language', 'The Java language, new features, and the JVM.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111', 'SERVER_SIDE_JAVA', 'Server-Side Java', 'Frameworks and libraries for server-side Java development.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', 'CLOUD', 'Cloud', 'Cloud-native development, Kubernetes, and serverless.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333304', '11111111-1111-1111-1111-111111111111', 'DEV_PRACTICES', 'Developer Practices', 'Testing, CI/CD, and modern development practices.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333305', '11111111-1111-1111-1111-111111111111', 'DATA_AI', 'Data & AI', 'Data engineering, machine learning, and AI.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333306', '11111111-1111-1111-1111-111111111111', 'SECURITY', 'Security', 'Application and infrastructure security.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333307', '11111111-1111-1111-1111-111111111111', 'ARCHITECTURE', 'Architecture', 'Software architecture, DDD, and system design.');
insert into track (id, cfp_id, trackCode, title, description)
values ('33333333-3333-3333-3333-333333333308', '11111111-1111-1111-1111-111111111111', 'NEW_AND_COOL', 'New & Cool', 'Emerging technologies and cool new stuff.');
