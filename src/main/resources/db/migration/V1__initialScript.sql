create table user
(
    id            uuid                       not null
        primary key,
    email         varchar(255)               not null,
    is_enabled    bit                        not null,
    main_currency enum ('BGN', 'EUR', 'USD') not null,
    password      varchar(255)               not null,
    phone_number  varchar(255)               not null,
    photo         varchar(255)               null,
    role          enum ('ADMIN', 'USER')     not null,
    username      varchar(255)               not null,
    constraint UK4bgmpi98dylab6qdvf9xyaxu4
        unique (phone_number),
    constraint UKob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint UKsb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table credit_card
(
    id              uuid         not null
        primary key,
    card_holder     varchar(255) not null,
    card_number     int          not null,
    cvv             int          not null,
    expiration_date datetime(6)  not null,
    is_deleted      bit          not null,
    user_id         uuid         not null,
    constraint UKdhq9mk9claw6xxkuitkchsr77
        unique (card_number),
    constraint FKh4wi9724muee2kp2c4ku1yia2
        foreign key (user_id) references user (id)
);

create table wallet
(
    id              uuid           not null
        primary key,
    balance         decimal(38, 2) not null,
    currency        varchar(255)   not null,
    is_deleted      bit            not null,
    wallet_owner_id uuid           not null,
    constraint UKfrc9w7o7cp34ldwubnh10kbxe
        unique (wallet_owner_id),
    constraint FKncgtorb8ndkw3u9v8anc4txmm
        foreign key (wallet_owner_id) references user (id)
);

create table transaction
(
    id                  uuid                                      not null
        primary key,
    amount              decimal(38, 2)                            not null,
    date                datetime(6)                               not null,
    status              enum ('APPROVED', 'DECLINED')             not null,
    type                enum ('EXCHANGE', 'TRANSFER', 'WITHDRAW') not null,
    recipient_wallet_id uuid                                      not null,
    sender_wallet_id    uuid                                      not null,
    constraint FK3riusbq7l7tpp0fyalvx1oxnl
        foreign key (sender_wallet_id) references wallet (id),
    constraint FK4vsdvbpxgej01e8xchb8w3hf5
        foreign key (recipient_wallet_id) references wallet (id)
);

