-- 스크립트 내용을 기반으로 checksum 생성
-- 따라서 조금이라도 변경하면 다른 checksum을 생성후 실행하는 동안 checksum 비교시 불일치 하게되면 오류 발생
-- 누군가 이 스크립트를 조작했다는 것을 알 수 있음
-- 즉 한번 완료된 스크립트는 변경하면 안됨
-- 변경할 것이 있으면 다음 시퀀스 번호로 변경을 해야함
INSERT INTO users (email, password, name, role)
VALUES ('admin@gmail.com', 'admin', 'Administrator', 'ROLE_ADMIN'),
       ('hyeon@gmail.com', 'secret', 'Hyeon', 'ROLE_USER');

INSERT INTO short_urls (short_key, original_url, created_by, created_at, expired_at, is_private, click_count)
VALUES ('hujfDf', 'https://mrk0607.tistory.com/2490', 1, TIMESTAMP '2024-10-04', NULL, FALSE, 0),
       ('rs1Aed', 'https://mrk0607.tistory.com/2493', 1, TIMESTAMP '2024-10-05', NULL, FALSE, 0),
       ('ertcbn', 'https://mrk0607.tistory.com/2501', 1, TIMESTAMP '2024-10-10', NULL, FALSE, 0),
       ('edfrtg', 'https://mrk0607.tistory.com/2510', 1, TIMESTAMP '2024-10-17', NULL, TRUE, 0),
       ('vbgtyh', 'https://mrk0607.tistory.com/2512', 1, TIMESTAMP '2024-10-18', NULL, FALSE, 0),
       ('rtyfgb', 'https://mrk0607.tistory.com/2516', 1, TIMESTAMP '2024-10-21', NULL, FALSE, 0),
       ('rtvbop', 'https://mrk0607.tistory.com/3030', 1, TIMESTAMP '2025-04-01', NULL, FALSE, 0),
       ('2wedfg', 'https://mrk0607.tistory.com/3032', 1, TIMESTAMP '2025-04-02', NULL, TRUE, 0),
       ('6yfrd4', 'https://mrk0607.tistory.com/3034', 1, TIMESTAMP '2025-04-04', NULL, FALSE, 0),
       ('r5t4tt', 'https://mrk0607.tistory.com/3036', 1, TIMESTAMP '2025-04-04', NULL, FALSE, 0),
       ('ffr4rt', 'https://mrk0607.tistory.com/3043', 1, TIMESTAMP '2025-04-08', NULL, FALSE, 0),
       ('9oui7u', 'https://mrk0607.tistory.com/2409', 1, TIMESTAMP '2025-04-09', NULL, FALSE, 0),
       ('cvbg5t', 'https://mrk0607.tistory.com/2315', 1, TIMESTAMP '2025-04-09', NULL, FALSE, 0),
       ('nm6ytf', 'https://mrk0607.tistory.com/2264', 1, TIMESTAMP '2025-04-10', NULL, FALSE, 0);
