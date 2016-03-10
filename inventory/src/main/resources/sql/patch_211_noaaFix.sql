update dataset_location_policy set base_path=replace(BASE_PATH, 'ftp://data.nodc', 'ftp://ftp.nodc')  where type='REMOTE-FTP';

update granule_reference set path=replace(PATH, 'ftp://data.nodc', 'ftp://ftp.nodc')  where type='REMOTE-FTP'

commit;
