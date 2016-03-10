<script type="text/javascript">
  // imports
  dojo.require("dojo.data.ItemFileReadStore");
  
  // data source
  var commonDataSourceUrls = new Object();
  commonDataSourceUrls['dataLatency'] = "${createLink(controller: 'dataLatency')}";
  commonDataSourceUrls['granuleStatus'] = "${createLink(controller: 'granuleStatus')}";
  commonDataSourceUrls['storageStatus'] = "${createLink(controller: 'storageStatus')}";
  commonDataSourceUrls['provider'] = "${createLink(controller: 'provider')}";
  
  /*
  // paging
  var commonPagingEnabled = ${flash['paging.enabled']};
  var commonPagingName = "${flash['paging.name']}";
  var commonNumberOfPages = ${flash['paging.numberOfPages']};
  var commonCurrentPage = ${flash['paging.current']};
  var commonTotalEntries = ${flash['paging.totalEntries']};
  */
  
  
  // functions
  /*
  function commonReceiveMessagePaging(url, page, onReceiveFunction, onErrorFunction) {
    var requestUrl = url;
    if(commonPagingEnabled) {
      requestUrl += '&'+commonPagingName+'='+page;
    }
    
    commonReceiveMessage(requestUrl, onReceiveFunction, onErrorFunction);
  }
  */
  
  function commonReceiveMessage(url, onReceivedFunction, onErrorFunction) {
    var store = new dojo.data.ItemFileReadStore({url: url});
    store.fetch({
      query: {type:"*"},
      urlPreventCache: true,
      onComplete: onReceivedFunction,
      onError: onErrorFunction
    });
  }
  
  function commonReadMessage(data, onReceivedFunction, onErrorFunction) {
    var store = new dojo.data.ItemFileReadStore({data: data});
    store.fetch({
      query: {type:"*"},
      onComplete: onReceivedFunction,
      onError: onErrorFunction
    });
  }
  
  function commonReceiveMessageForm(url, form, onReceivedFunction, onErrorFunction) {
    var call = {
      url: url,
      load: onReceivedFunction,
      error: onErrorFunction,
      timeout: 5000,
      form: form
    };
    dojo.xhrPost(call);
    /*
    var store = new dojo.data.ItemFileReadStore({url: url});
    store.fetch({
      query: {type:"*"},
      urlPreventCache: true,
      onComplete: onReceivedFunction,
      onError: onErrorFunction
    });
    */
  }
</script>
