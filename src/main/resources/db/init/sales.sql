create table if not exists "Sales"
(
    id             bigint generated by default as identity not null,
    created_at     timestamp with time zone                null     default now(),
    description    text                                    not null default ''::text,
    telegram_id    bigint                                  not null,
    found_in_group bigint                                  not null,
    constraint Sales_pkey primary key (id)
) tablespace pg_default;