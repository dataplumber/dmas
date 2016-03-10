create or replace
PACKAGE INGEST as

 PROCEDURE updateStorage(totalSize in NUMBER, lastMod in NUMBER, engineId in NUMBER);

 FUNCTION getEngine(engineType in VARCHAR2, totalSize in NUMBER) return Integer;


END INGEST;

/

create or replace
PACKAGE BODY INGEST AS

procedure updateStorage(totalSize in NUMBER, lastMod in NUMBER, engineId in NUMBER)
 is
  BEGIN

     update ING_STORAGE set last_used=lastMod, space_used=(space_used+totalSize)  where id=engineId ;

    return;
END updateStorage;

FUNCTION getEngine(engineType in VARCHAR2, totalSize in NUMBER)
return INTEGER AS

vEid ing_storage.id%TYPE;
begin

select insid into vEid  from (select ins.id as insid, (ins.space_reserved - (ins.space_used + totalSize)) as sz from ING_STORAGE ins where sz > 0  AND  ins.id in (select id from ING_ENGINE where stereotype = engineType)  order by sz desc) where rownum = 1;


return vEid;

end getEngine;

END INGEST;

