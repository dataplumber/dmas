insert into collection_legacy_product((select collection_id, product_id from collection_product where product_id is not null));

commit;
