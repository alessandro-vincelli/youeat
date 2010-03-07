ALTER TABLE activity_ristorante ADD COLUMN ristorante_fk character varying(36) NOT NULL;

update activity_ristorante  set ristorante_fk  = ristorante ;
ALTER TABLE activity_ristorante ALTER COLUMN ristorante_fk SET NOT NULL;


--ALTER TABLE activity_ristorante DROP COLUMN ristorante;
-- set to not null ristorante_fk


alter table activity_ristorante 
        add constraint FK93343C9F70781DD 
        foreign key (ristorante_fk) 
        references ristorante;

ALTER TABLE activity_ristorante DROP CONSTRAINT fk93343c9aa76a44b;

--ALTER TABLE activity_ristorante DROP CONSTRAINT FK709D5753CC602F0E;
--ALTER TABLE activity_ristorante DROP CONSTRAINT FK709D5753AA76A44B;

drop table ristorante_activities;
