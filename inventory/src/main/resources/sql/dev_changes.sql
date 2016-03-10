update dataset_location_policy set base_path=replace(base_path, 'file:///store', 'file:///data/dev/users/podaacdev/data/archive/store') where type like 'ARCHIVE%';
