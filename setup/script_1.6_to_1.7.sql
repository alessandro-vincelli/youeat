ALTER TABLE ristorante ADD COLUMN restaurateur character varying(36);   

alter table ristorante 
        add constraint FKFF1E1099C7984FD1 
        foreign key (restaurateur) 
        references eater;

create table restaurateur_blackboardi18n (
        id varchar(36) not null,
        blackboard varchar(10000),
        language varchar(36),
        primary key (id)
    );
        
create table ristorante_restaurateur_blackboards (
        ristorante varchar(36) not null,
        restaurateur_blackboards varchar(36) not null,
        unique (restaurateur_blackboards)
    );      
        
alter table restaurateur_blackboardi18n 
        add constraint FK834A37DFA2A79C09 
        foreign key (language) 
        references language;
        

alter table ristorante_restaurateur_blackboards 
        add constraint FK26333381B052C55 
        foreign key (restaurateur_blackboards) 
        references restaurateur_blackboardi18n;

alter table ristorante_restaurateur_blackboards 
        add constraint FK2633338AA76A44B 
        foreign key (ristorante) 
        references ristorante;

