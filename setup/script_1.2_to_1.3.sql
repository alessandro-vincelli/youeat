
    
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
    
    create index idx_eater_id on eater (id);
    
    create index idx_eater_relation_type on eater_relation (type);

    create index idx_eater_relation_from_user on eater_relation (from_user);

    create index idx_eater_relation_id on eater_relation (id);

    create index idx_eater_relation_status on eater_relation (status);

    create index idx_eater_relation_to_user on eater_relation (to_user);
    
    create index idx_ristorante_position_ristorante on ristorante_position (ristorante);

    alter table ristorante_position 
        add constraint FKEB90560FAA76A44B 
        foreign key (ristorante) 
        references ristorante;
	
    ALTER TABLE ristorante ADD COLUMN email character varying(255);
