CREATE SCHEMA IF NOT EXISTS virtual_wallet;
Use virtual_wallet;

create table user
(
    id           uuid                                             not null
        primary key,
    created_at   datetime(6)                                      null,
    deleted_at   datetime(6)                                      null,
    email        varchar(255)                                     not null,
    first_name   varchar(255)                                     not null,
    last_name    varchar(255)                                     not null,
    password     varchar(255)                                     not null,
    phone_number varchar(255)                                     not null,
    photo        varchar(255)                                     null,
    role         enum ('ADMIN', 'USER')                           not null,
    status       enum ('ACTIVE', 'BLOCKED', 'DELETED', 'PENDING') not null,
    username     varchar(255)                                     not null,
    constraint UK4bgmpi98dylab6qdvf9xyaxu4
        unique (phone_number),
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table card
(
    id              uuid                 not null
        primary key,
    card_holder     varchar(255)         not null,
    card_number     varchar(255)         not null,
    created_at      datetime(6)          not null,
    cvv             varchar(5)           not null,
    deleted_at      datetime(6)          null,
    expiration_date varchar(10)          not null,
    is_deleted      tinyint(1) default 0 not null,
    owner_id        uuid                 not null,
    constraint UKby1nk98m2hq5onhl68bo09sc1
        unique (card_number),
    constraint FK8pspfj8x9rbqn67t0l8ir7im3
        foreign key (owner_id) references user (id)
);

create table email_confirmation_token
(
    id           uuid        not null
        primary key,
    confirmed_at datetime(6) null,
    created_at   datetime(6) not null,
    expires_at   datetime(6) not null,
    user_id      uuid        not null,
    constraint FKk1kk7ut1owm1c4ymjfxr1nr8f
        foreign key (user_id) references user (id)
);

create table wallet
(
    id         uuid                       not null
        primary key,
    balance    decimal(38, 2)             not null,
    created_at datetime(6)                not null,
    currency   enum ('BGN', 'EUR', 'USD') not null,
    deleted_at datetime(6)                null,
    is_deleted tinyint(1) default 0       not null,
    owner_id   uuid                       not null,
    constraint FKrg4reqrefjux3h25jrga2dc0p
        foreign key (owner_id) references user (id)
);

create table exchange
(
    id                 uuid                       not null
        primary key,
    amount             decimal(38, 2)             not null,
    to_amount          decimal(38, 2)             not null,
    exchange_rate      decimal(38, 10)            not null,
    from_currency      enum ('BGN', 'EUR', 'USD') not null,
    to_currency        enum ('BGN', 'EUR', 'USD') not null,
    recipient_username varchar(255)               not null,
    from_wallet_id     uuid                       not null,
    to_wallet_id       uuid                       not null,
    constraint FK1nbx3jk9g0rr5xn270ep1vyar
        foreign key (from_wallet_id) references wallet (id),
    constraint FK9suobht6regbvvu04anyfd43b
        foreign key (to_wallet_id) references wallet (id),
    date               datetime(6)                not null
);

create table transaction
(
    id                  uuid                       not null
        primary key,
    amount              decimal(38, 2)             not null,
    currency            enum ('BGN', 'EUR', 'USD') not null,
    date                datetime(6)                not null,
    recipient_wallet_id uuid                       not null,
    sender_wallet_id    uuid                       not null,
    constraint FK3riusbq7l7tpp0fyalvx1oxnl
        foreign key (sender_wallet_id) references wallet (id),
    constraint FK4vsdvbpxgej01e8xchb8w3hf5
        foreign key (recipient_wallet_id) references wallet (id)
);

create table transfer
(
    id        uuid                          not null
        primary key,
    amount    decimal(38, 2)                not null,
    currency  enum ('BGN', 'EUR', 'USD')    not null,
    date      datetime(6)                   not null,
    status    enum ('APPROVED', 'DECLINED') not null,
    card_id   uuid                          not null,
    wallet_id uuid                          not null,
    constraint FKt1ux5tr1t6r8ow1khvm5w3j2w
        foreign key (card_id) references card (id),
    constraint FKtdhfxaei7nqto932210wmbmtk
        foreign key (wallet_id) references wallet (id)
);


create table exchange_rate
(
    id            uuid                       not null
        primary key,
    rate          decimal(38, 10)            not null,
    from_currency enum ('BGN', 'EUR', 'USD') not null,
    to_currency   enum ('BGN', 'EUR', 'USD') not null
);

