
    create table activity (
        id varchar(36) not null,
        date timestamp not null,
        eater varchar(36) not null,
        primary key (id),
        unique (id)
    );

    create table activity_eater_relation (
        id varchar(36) not null,
        date timestamp not null,
        eater_activity_type varchar(255),
        eater varchar(36) not null,
        with_user varchar(36) not null,
        primary key (id)
    );

    create table activity_ristorante (
        id varchar(36) not null,
        date timestamp not null,
        type varchar(255) not null,
        eater varchar(36) not null,
        ristorante_fk varchar(36) not null,
        primary key (id)
    );

    create table city (
        id varchar(36) not null,
        latitude varchar(255),
        longitude varchar(255),
        name varchar(255),
        name_simplified varchar(255),
        region varchar(255),
        country varchar(36) not null,
        primary key (id)
    );

    create table comment (
        id varchar(36) not null,
        body varchar(1000),
        creation_time timestamp not null,
        enabled bool not null,
        title varchar(160),
        author varchar(36),
        primary key (id)
    );

    create table country (
        id varchar(36) not null,
        iso2 varchar(255),
        iso3 varchar(255),
        name varchar(255),
        primary key (id)
    );

    create table country_region (
        id varchar(36) not null,
        iso2 varchar(255),
        iso3 varchar(255),
        name varchar(255),
        region varchar(255),
        primary key (id)
    );

    create table data_ristorante (
        id varchar(36) not null,
        address varchar(255),
        city varchar(255),
        country varchar(255),
        creation_time timestamp,
        description varchar(255),
        fax_number varchar(255),
        mobile_phone_number varchar(255),
        name varchar(255),
        phone_number varchar(255),
        postal_code varchar(255),
        province varchar(255),
        type varchar(255),
        www varchar(255),
        primary key (id)
    );

    create table dialog (
        id varchar(36) not null,
        creation_time timestamp not null,
        deleted_from_receiver bool not null,
        deleted_from_sender bool not null,
        is_private bool not null,
        receiver varchar(36),
        sender varchar(36),
        primary key (id)
    );

    create table eater (
        id varchar(36) not null,
        avatar bytea,
        blocked bool not null,
        confirmed bool not null,
        creation_time timestamp not null,
        email varchar(255) not null,
        firstname varchar(255) not null,
        lastname varchar(255) not null,
        password varchar(255) not null,
        password_salt varchar(255) not null,
        sex int4,
        social_type int4,
        socialuid varchar(255),
        version int4 not null,
        country varchar(36) not null,
        language varchar(36) not null,
        user_profile varchar(36) not null,
        primary key (id),
        unique (email)
    );

    create table eater_profile (
        id varchar(36) not null,
        description varchar(255),
        name varchar(255),
        primary key (id),
        unique (name)
    );

    create table eater_relation (
        id varchar(36) not null,
        end_date timestamp,
        start_date timestamp,
        status varchar(255),
        type varchar(255),
        from_user varchar(36),
        to_user varchar(36),
        primary key (id)
    );

    create table language (
        id varchar(36) not null,
        country varchar(255),
        language varchar(255),
        primary key (id)
    );

    create table message (
        id varchar(36) not null,
        body varchar(10000),
        read_time timestamp,
        sent_time timestamp not null,
        title varchar(160),
        dialog_id varchar(36) not null,
        sender varchar(36),
        primary key (id)
    );

    create table prov_ita (
        prov varchar(255) not null,
        primary key (prov)
    );

    create table rate_on_ristorante (
        id varchar(36) not null,
        rate int4 not null,
        primary key (id)
    );

    create table ristorante (
        id varchar(36) not null,
        address varchar(255),
        creation_time timestamp,
        description varchar(10000),
        email varchar(255),
        fax_number varchar(255),
        latitude float8 not null,
        longitude float8 not null,
        mobile_phone_number varchar(255),
        modification_time timestamp,
        name varchar(255),
        phone_number varchar(255),
        postal_code varchar(255),
        province varchar(255),
        revision_number int4 not null,
        type varchar(255),
        version int4 not null,
        www varchar(255),
        city varchar(36),
        country varchar(36),
        types varchar(36),
        primary key (id)
    );

    create table ristorante_comments (
        ristorante varchar(36) not null,
        comments varchar(36) not null,
        unique (comments)
    );

    create table ristorante_descriptioni18n (
        id varchar(36) not null,
        description varchar(10000),
        language varchar(36),
        primary key (id)
    );

    create table ristorante_descriptions (
        ristorante varchar(36) not null,
        descriptions varchar(36) not null,
        unique (descriptions)
    );

    create table ristorante_picture (
        id varchar(36) not null,
        active bool not null,
        description varchar(255),
        picture bytea,
        title varchar(255),
        primary key (id)
    );

    create table ristorante_pictures (
        ristorante varchar(36) not null,
        pictures varchar(36) not null,
        unique (pictures)
    );

    create table ristorante_position (
        id varchar(36) not null,
        cos_latitude float8 not null,
        cos_longitude float8 not null,
        latitude float8,
        longitude float8 not null,
        sin_latitude float8 not null,
        sin_longitude float8 not null,
        ristorante varchar(36) not null,
        primary key (id),
        unique (ristorante)
    );

    create table ristorante_rates (
        ristorante varchar(36) not null,
        rates varchar(36) not null,
        unique (rates)
    );

    create table ristorante_revision (
        id varchar(36) not null,
        address varchar(255),
        creation_time timestamp,
        latitude float8 not null,
        longitude float8 not null,
        mobile_phone_number varchar(255),
        modification_time timestamp,
        name varchar(255),
        phone_number varchar(255),
        postal_code varchar(255),
        province varchar(255),
        revision_number int4 not null,
        type varchar(255),
        www varchar(255),
        city varchar(36),
        country varchar(36),
        primary key (id)
    );

    create table ristorante_revision_descriptions (
        ristorante_revision varchar(36) not null,
        descriptions varchar(36) not null,
        unique (descriptions)
    );

    create table ristorante_revisions (
        ristorante varchar(36) not null,
        revisions varchar(36) not null,
        unique (revisions)
    );

    create table ristorante_tags (
        ristorante varchar(36) not null,
        tags varchar(36) not null
    );

    create table ristorante_types (
        id varchar(36) not null,
        bar bool not null,
        pizzeria bool not null,
        ristorante bool not null,
        primary key (id)
    );

    create table tag (
        id varchar(36) not null,
        tag varchar(255),
        primary key (id),
        unique (tag)
    );

    alter table activity 
        add constraint activity_to_eater_fk 
        foreign key (eater) 
        references eater;

    alter table activity_eater_relation 
        add constraint activity_relation_with_user_fk 
        foreign key (with_user) 
        references eater;

    alter table activity_eater_relation 
        add constraint activity_to_eater_fk 
        foreign key (eater) 
        references eater;

    alter table activity_ristorante 
        add constraint FK93343C9F70781DD 
        foreign key (ristorante_fk) 
        references ristorante;

    alter table activity_ristorante 
        add constraint activity_to_eater_fk 
        foreign key (eater) 
        references eater;

    create index city_name_idx on city (name);

    alter table city 
        add constraint city_to_country_fk 
        foreign key (country) 
        references country;

    alter table comment 
        add constraint comment_to_author_fk 
        foreign key (author) 
        references eater;

    create index countryname on country (name);

    create index iso2name on country (iso2);

    alter table dialog 
        add constraint message_to_sender_author_fk 
        foreign key (sender) 
        references eater;

    alter table dialog 
        add constraint message_to_receiver_author_fk 
        foreign key (receiver) 
        references eater;

    create index idx_eater_id on eater (id);

    alter table eater 
        add constraint eater_to_profile_fk 
        foreign key (user_profile) 
        references eater_profile;

    alter table eater 
        add constraint eater_to_langiage_fk 
        foreign key (language) 
        references language;

    alter table eater 
        add constraint FK5BD1E0548F9C981 
        foreign key (country) 
        references country;

    create index idx_eater_relation_type on eater_relation (type);

    create index idx_eater_relation_from_user on eater_relation (from_user);

    create index idx_eater_relation_id on eater_relation (id);

    create index idx_eater_relation_status on eater_relation (status);

    create index idx_eater_relation_to_user on eater_relation (to_user);

    alter table eater_relation 
        add constraint eaterrelation_to_toeater_fk 
        foreign key (to_user) 
        references eater;

    alter table eater_relation 
        add constraint eater_to_eaterRelation_fk 
        foreign key (from_user) 
        references eater;

    create index idx_massage_to_dialog on message (dialog_id);

    create index idx_massage_sender on message (sender);

    create index idx_message_readtime on message (sent_time);

    alter table message 
        add constraint message_to_sender_author_fk 
        foreign key (sender) 
        references eater;

    alter table message 
        add constraint FK38EB0007C4270453 
        foreign key (dialog_id) 
        references dialog;

    create index risto_revisionNumber_index on ristorante (revision_number);

    create index risto_modificationtime_index on ristorante (modification_time);

    create index risto_name_index on ristorante (name);

    alter table ristorante 
        add constraint FKFF1E10996D9740A1 
        foreign key (city) 
        references city;

    alter table ristorante 
        add constraint FKFF1E1099BF3DAC60 
        foreign key (types) 
        references ristorante_types;

    alter table ristorante 
        add constraint FKFF1E109948F9C981 
        foreign key (country) 
        references country;

    alter table ristorante_comments 
        add constraint FK9B159ADAC55AC7DA 
        foreign key (comments) 
        references comment;

    alter table ristorante_comments 
        add constraint FK9B159ADAAA76A44B 
        foreign key (ristorante) 
        references ristorante;

    alter table ristorante_descriptioni18n 
        add constraint FKC670B474A2A79C09 
        foreign key (language) 
        references language;

    alter table ristorante_descriptions 
        add constraint FKEBE3D5DDAA76A44B 
        foreign key (ristorante) 
        references ristorante;

    alter table ristorante_descriptions 
        add constraint FKEBE3D5DD60461A1F 
        foreign key (descriptions) 
        references ristorante_descriptioni18n;

    alter table ristorante_pictures 
        add constraint FK9379001BAA76A44B 
        foreign key (ristorante) 
        references ristorante;

    alter table ristorante_pictures 
        add constraint FK9379001BFE417821 
        foreign key (pictures) 
        references ristorante_picture;

    create index idx_ristorante_position_ristorante on ristorante_position (ristorante);

    alter table ristorante_position 
        add constraint FKEB90560FAA76A44B 
        foreign key (ristorante) 
        references ristorante;

    alter table ristorante_rates 
        add constraint FK5A7C2C6DDB4C1CC4 
        foreign key (rates) 
        references rate_on_ristorante;

    alter table ristorante_rates 
        add constraint FK5A7C2C6DAA76A44B 
        foreign key (ristorante) 
        references ristorante;

    create index ristoRevision_modificationtime_index on ristorante_revision (modification_time);

    create index ristoRevision_revisionnumber_index on ristorante_revision (revision_number);

    alter table ristorante_revision 
        add constraint FKAF7274C16D9740A1 
        foreign key (city) 
        references city;

    alter table ristorante_revision 
        add constraint FKAF7274C148F9C981 
        foreign key (country) 
        references country;

    alter table ristorante_revision_descriptions 
        add constraint FK517972B5DA6C526E 
        foreign key (ristorante_revision) 
        references ristorante_revision;

    alter table ristorante_revision_descriptions 
        add constraint FK517972B560461A1F 
        foreign key (descriptions) 
        references ristorante_descriptioni18n;

    alter table ristorante_revisions 
        add constraint FK3EDC23D2491C21A5 
        foreign key (revisions) 
        references ristorante_revision;

    alter table ristorante_revisions 
        add constraint FK3EDC23D2AA76A44B 
        foreign key (ristorante) 
        references ristorante;

    alter table ristorante_tags 
        add constraint FK1BB253FFA135CABA 
        foreign key (tags) 
        references tag;

    alter table ristorante_tags 
        add constraint FK1BB253FFAA76A44B 
        foreign key (ristorante) 
        references ristorante;
