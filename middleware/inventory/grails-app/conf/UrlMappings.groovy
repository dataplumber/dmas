class UrlMappings {

	static mappings = {
		"/collection/$id"(controller:"collection"){action=[GET:"show"]}
		"/collection/dataset/$id"(controller:"collection"){action=[GET:"collectionByDataset"]}
		"/collection/list"(controller:"collection"){action=[GET:"list"]}
		"/collections"(controller:"collection"){action=[GET:"list"]}
		"/collection/$id/product"(controller:"collection"){action=[POST:"updateCollectionProduct", PUT:"updateCollectionProduct"]}
		
		"/element/$id"(controller:"element"){action=[GET:"show"]}
		
		"/contact/$id"(controller:"contact"){action=[GET:"show"]}
		"/contacts"(controller:"contact"){action=[GET:"list"]}
		
		"/dataset/$id/echo"(controller:"dataset"){action=[GET:"echoGranules"]}
		"/dataset/$id/sog"(controller:"dataset"){action=[GET:"sizeOfGranule"]}
		"/dataset/$id/coverage"(controller:"dataset"){action=[GET:"coverage"]}
		"/dataset/$id/policy"(controller:"dataset"){action=[GET:"policy"]}
		"/dataset/$id/latestGranule"(controller:"dataset"){action=[GET:"latestGranule"]}
		"/dataset/$id/aip"(controller:"dataset"){action=[GET:"getAIP"]}
		"/dataset/$id/granuleSize"(controller:"dataset"){action=[GET:"sizeOfGranule"]}
		"/datasets"(controller:"dataset"){action=[GET:"list"]}
        "/dataset/$id"(controller: "dataset"){action=[GET:"show"]}
		"/dataset/$id/granuleList"(controller:"dataset"){action=[GET:"listOfGranules"]}
		"/dataset/$id/granuleReferences"(controller:"dataset"){action=[GET:"listOfGranuleReferences"]}
		"/dataset/product/$id"(controller:"dataset"){action=[GET:"findByProductId"]}
		"/heartbeat"(controller:"dataset"){action=[GET:"heartbeat"]}
		"/sources/"(controller:"source"){action=[GET:"list"]}		
		"/sensors/"(controller:"sensor"){action=[GET:"list"]}
		//"/granule/list/aip/?"(controller:"granule"){action=[GET:"findGranuleAIPList"]}//findGranuleList
		"/granules/listById"(controller:"granule"){action=[GET:"findGranuleList"]}//findGranuleList
		"/granules"(controller:"granule"){action=[GET:"list"]}//list
		"/granule/$id/granule/rootpath"(controller:"granule"){action=[POST:"updateGranuleRootPath"]}
		"/granule/$id/granule/reassociate"(controller:"granule"){action=[POST:"updateReassociateGranule"]}
		"/granule/$id/granule/element/reassociate"(controller:"granule"){action=[POST:"updateReassociateGranuleElement"]}
		"/granule/$id/reference/local"(controller:"granule"){action=[DELETE:"deleteGranuleReference"]}
		"/granule/$id/reference/path"(controller:"granule"){action=[POST:"updateGranuleReferencePath"]}
		"/granule/$id/reference/status"(controller:"granule"){action=[POST:"updateGranuleReferenceStatus"]}
		"/granule/$id/reference/newReference"(controller:"granule"){action=[POST:"addNewGranuleReference"]}
		"/granule/$id/aipReference"(controller:"granule"){action=[POST:"updateAIPRef"]}
		"/granule/$id/statAndVerify"(controller:"granule"){action=[POST:"updateGranStatusAndVerify"]}
		"/granule/$id/aipArchive"(controller:"granule"){action=[POST:"updateAIPArch"]}
		"/granule/$id/archive/checksum"(controller:"granule"){action=[POST:"updateArchiveChecksum"]}
		"/granule/$id/archive/size"(controller:"granule"){action=[POST:"updateArchiveSize"]}
		"/granule/$id/gmh/echo"(controller:"granule"){action=[POST:"updateEchoSubmitTime"]}
		"/granule/$id/gmh"(controller:"granule"){action=[GET:"gmh"]}
		"/granule/$id/spatial"(controller:"granule"){action=[GET:"spatial"]}
		"/granule/$id/archive/status"(controller:"granule"){action=[POST:"updateArchiveStatus"]}
		"/granule/$id"(controller:"granule"){action=[GET:"show",DELETE:"delete"]}
		"/granule/$id/archivePath"(controller:"granule"){action=[GET:"fetchArchivePath"]}
		"/granule/$id/status"(controller:"granule"){action=[POST:"updateGranuleStatus"]}
		"/provider/$id"(controller:"provider"){action=[GET:"show"]}
		"/providers"(controller:"provider"){action=[GET:"list"]}
//		"/$controller/$action?/$id?"{
//			constraints {
//				// apply constraints here
//			}
//		}
		"/manifest"(controller:"manifest"){action=[POST:"processManifest",PUT:"processManifest",GET:"processManifest"]}
        "/DMTmanifest"(controller:"manifest"){action=[POST:"processManifestDMT",PUT:"processManifestDMT",GET:"processManifestDMT"]}
		"/sip/"(controller:"sip"){action=[PUT:"addSip",POST:"addSip"]}
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
