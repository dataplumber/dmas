delete from dataset_real where de_id in(
select de_id from  dataset_element where dataset_element.scope='BOTH' or dataset_element.scope='DATASET');

delete from dataset_character where de_id in(
select de_id from  dataset_element where dataset_element.scope='BOTH' or dataset_element.scope='DATASET');

delete from dataset_datetime where de_id in(
select de_id from  dataset_element where dataset_element.scope='BOTH' or dataset_element.scope='DATASET');

delete from dataset_integer where de_id in(
select de_id from  dataset_element where dataset_element.scope='BOTH' or dataset_element.scope='DATASET');

update dataset_element set scope='GRANULE' where scope='BOTH' or scope='DATASET';
