create table user
(
    id           uuid                   not null
        primary key,
    created_at   datetime(6)            null,
    deleted_at   datetime(6)            null,
    email        varchar(255)           not null,
    first_name   varchar(255)           not null,
    last_name    varchar(255)           not null,
    password     varchar(255)           not null,
    phone_number varchar(255)           not null,
    photo        varchar(255)           null,
    role         enum ('ADMIN', 'USER') not null,
    username     varchar(255)           not null,
    status       enum ('PENDING', 'ACTIVE', 'BLOCKED', 'DELETED') default 'PENDING' not null,
    constraint UK4bgmpi98dylab6qdvf9xyaxu4
        unique (phone_number),
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table credit_card
(
    id              uuid                 not null
        primary key,
    card_holder     varchar(255)         not null,
    card_number     varchar(255)         not null,
    created_at      datetime(6)          not null,
    cvv             varchar(255)         not null,
    deleted_at      datetime(6)          null,
    expiration_date datetime(6)          not null,
    is_deleted      tinyint(1) default 0 not null,
    owner_id        uuid                 not null,
    constraint UKdhq9mk9claw6xxkuitkchsr77
        unique (card_number),
    constraint FKerr457ghbr3vkx6cslnwmget5
        foreign key (owner_id) references user (id)
);

create table wallet
(
    id         uuid                       not null
        primary key,
    balance    decimal(15, 2)             not null,
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
    id             uuid                       not null
        primary key,
    amount         decimal(15, 2)             not null,
    currency       enum ('BGN', 'EUR', 'USD') not null,
    date           datetime(6)                not null,
    exchange_rate  decimal(38, 2)             not null,
    from_wallet_id uuid                       not null,
    to_wallet_id   uuid                       not null,
    constraint FK1nbx3jk9g0rr5xn270ep1vyar
        foreign key (from_wallet_id) references wallet (id),
    constraint FK9suobht6regbvvu04anyfd43b
        foreign key (to_wallet_id) references wallet (id)
);

create table transaction
(
    id                  uuid                       not null
        primary key,
    amount              decimal(15, 2)             not null,
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
    id             uuid                          not null
        primary key,
    amount         decimal(38, 2)                not null,
    currency       enum ('BGN', 'EUR', 'USD')    not null,
    date           datetime(6)                   not null,
    status         enum ('APPROVED', 'DECLINED') not null,
    credit_card_id uuid                          not null,
    wallet_id      uuid                          not null,
    constraint FKi8wgfuwe9t6p8jss1cyisp0q2
        foreign key (credit_card_id) references credit_card (id),
    constraint FKtdhfxaei7nqto932210wmbmtk
        foreign key (wallet_id) references wallet (id)
);

