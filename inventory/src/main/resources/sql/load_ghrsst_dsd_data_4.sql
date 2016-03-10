/*
** Copyright (c) 2007-2008, by the California Institute of Technology.
** ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
**
** $Id: load_ghrsst_dsd_data_4.sql 1193 2008-05-26 21:29:32Z shardman $
*/

/*
** This script loads the GHRSST data from the dsd_mmr database into 
** the Inventory schema. This script requires a schema named 
** "ghrsst_migration" to be located on the same Oracle database server 
** which contains the GHRSST dsd_mmr and fr_mmr schemas.
*/

/*
** contact
**
** Extract the distinct entries into our contact table.
*/

DECLARE
  role      contact_temp.role%TYPE;
  f_name    contact_temp.first_name%TYPE;
  m_name    contact_temp.middle_name%TYPE;
  l_name    contact_temp.last_name%TYPE; 
  email     contact_temp.email%TYPE;
  phone     contact_temp.phone%TYPE;
  fax       contact_temp.fax%TYPE;
  address   contact_temp.address%TYPE;
     
CURSOR DCursor IS
  SELECT DISTINCT role, first_name, middle_name, last_name, email, phone, fax, address  
  FROM contact_temp; 

BEGIN
  OPEN DCursor;
  LOOP

  FETCH DCursor INTO role, f_name, m_name, l_name, email, phone, fax, address;
  EXIT WHEN DCursor%NOTFOUND;

  INSERT INTO contact (contact_id, role, first_name, middle_name, last_name, email, phone, fax, address)
  VALUES (contact_id_seq.nextval, role, f_name, m_name, l_name, email, phone, fax, address);
 
  END LOOP;
  CLOSE DCursor;
END;
/
