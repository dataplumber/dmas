class IngEventCategory {

   String name

   static constraints = {
      name(unique: true, size: 1..100)
   }

   static mapping = {
      //version false
      id generator:'sequence', params:[sequence:'ing_event_category_id_seq']            
   }
}
