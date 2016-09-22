CREATE TABLE `f_taluka` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`district_id` BIGINT(20) NOT NULL,
	`iso_taluka_code` CHAR(3) NULL DEFAULT NULL,
	`taluka_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `UQ_sid_iso_taluka_code_name` (`district_id`, `iso_taluka_code`, `taluka_name`),
	INDEX `FK_district_id` (`district_id`),
	INDEX `INX_iso_taluka_code` (`iso_taluka_code`),
	INDEX `INX_taluka_name` (`taluka_name`),
	CONSTRAINT `FK_district_id` FOREIGN KEY (`district_id`) REFERENCES `f_district` (`id`)
)AUTO_INCREMENT=1;

ALTER TABLE `f_address`
	CHANGE COLUMN `taluka` `taluka_id` BIGINT(20) NULL DEFAULT NULL AFTER `village_town`,
	ADD CONSTRAINT `FK_f_address_taluka_id` FOREIGN KEY (`taluka_id`) REFERENCES `f_taluka` (`id`);
	
INSERT IGNORE INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES 
('gis', 'CREATE_TALUKA', 'TALUKA', 'CREATE', 0),
('gis', 'CREATE_TALUKA_CHECKER', 'TALUKA', 'CREATE_CHECKER', 0),
('gis', 'UPDATE_TALUKA', 'TALUKA', 'UPDATE', 0),
('gis', 'UPDATE_TALUKA_CHECKER', 'TALUKA', 'UPDATE_CHECKER', 0),
('gis', 'DELETE_TALUKA', 'TALUKA', 'DELETE', 0),
('gis', 'DELETE_TALUKA_CHECKER', 'TALUKA', 'DELETE_CHECKER', 0),
('gis', 'READ_TALUKA', 'TALUKA', 'READ', 0);



