update element_dd set short_name='westernmostLongitude' where short_name='westLongitude';
update element_dd set short_name='easternmostLongitude' where short_name='eastLongitude';
update element_dd set short_name='southernmostLatitude' where short_name='southLatitude';
update element_dd set short_name='northernmostLatitude' where short_name='northLatitude';

INSERT into dataset_element (de_id, dataset_id,element_id,obligation_flag, scope)  (select dataset_element_id_seq.nextval,dataset_id, element_id, 'O', 'GRANULE' from dataset, element_dd where element_dd.short_name in ('version','ancillaryName','revolution','cycle','pass','passType','dayNightType','dayOfYearStart','comment'));

delete from granule_datetime where de_id in (select de_id from dataset_element where element_id in (select element_id from element_dd where short_name='startTime' or short_name='stopTime'));
delete from dataset_datetime where de_id in (select de_id from dataset_element where element_id in (select element_id from element_dd where short_name='startTime' or short_name='stopTime'));
delete from dataset_element where element_id in (select element_id from element_dd where short_name='startTime' or short_name='stopTime');

