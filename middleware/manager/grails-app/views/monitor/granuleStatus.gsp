<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <g:render template="/common/head" />
    <g:render template="/common/dojo" />
    <g:render template="/common/css" />
    <g:render template="/common/javaScript" />
    <script type="text/javascript">
      //dojo.require("dojo._base.lang");
      //dojo.require("dojo._base.html");
      dojo.require("dojo.parser");
      dojo.require("dojo.date");
      dojo.require("dijit.form.Button");
      dojo.require("dijit.form.Form");
      dojo.require("dijit.form.ValidationTextBox");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.SplitContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dijit.form.TextBox");
      dojo.require("dijit.Dialog");
    </script>
    <style type="text/css">
      #loadContainer {
        width: 54px;
        margin-left: auto;
        margin-right: auto;
        /*margin: 20px 0px;*/
      }
      
      /*
      #loadButtonContainer {
        width: 150px;
        height: 40px;
        float: left;
      }
      */
      
      #loadIconContainer {
        width: 24px;
        padding: 30px 30px;
        float: left;
        visibility: hidden;
      }
      
      /*
      #loadMessageContainer {
        width: 416px;
        padding: 15px 15px;
        float: left;
        visibility: hidden;
      }
      */
      
      #messageTable {
        margin-left: auto;
        margin-right: auto;
        width: 780px;
        opacity: 0;
        clear: both;
      }
      
      #messageTableNavigator {
        width: 780px;
        clear: both;
      }
      
      #messageTableNavigatorNewest {
        float: left;
        padding: 5px 10px;
      }
      
      #messageTableNavigatorNewer {
        float: left;
        padding: 5px 10px;
      }
      
      #messageTableNavigatorOlder {
        float: left;
        padding: 5px 10px;
      }
      
      #messageTableNavigatorOldest {
        float: left;
        padding: 5px 10px;
      }
      
      #messageTableNavigatorFrom {
        float: left;
        padding: 5px 10px;
        font-weight: bold;
      }
      
      #messageTableNavigatorTo {
        float: left;
        padding: 5px 10px;
        font-weight: bold;
      }
      
      #messageTableNavigatorTotal {
        float: left;
        padding: 5px 10px;
        font-weight: bold;
      }
      
      .messageTableNavigatorText {
        float: left;
        padding: 5px 10px;
      }
      
      .messageTableHeader {
        font-weight: bold;
        color: #fafafa;
        overflow: auto;
        clear: both;
      }
      
      .messageTableHeaderColumn-Importance {
        float: left;
        width: 100px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-GranuleName {
        float: left;
        width: 230px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-State {
        float: left;
        width: 120px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-Lock {
        float: left;
        width: 50px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableHeaderColumn-Updated {
        float: left;
        width: 180px;
        padding: 10px 10px;
        background-color: #4d4c4d;
      }
      
      .messageTableRow {
        overflow: auto;
      }
      
      .messageTableColumn-importance {
        float: left;
        width: 100px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-granuleName {
        float: left;
        width: 230px;
        height: 60px;
        padding: 10px 10px;
        overflow: auto;
      }
      
      .messageTableColumn-state {
        float: left;
        width: 120px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-lock {
        float: left;
        width: 50px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableColumn-updated {
        float: left;
        width: 180px;
        height: 60px;
        padding: 10px 10px;
      }
      
      .messageTableMessage {
        width: 760px;
        padding: 10px 10px;
        background-color: #efefef;
        overflow: auto;
      }
      
      .messageTable-High {
        background-color: #ffbdbd;
        border-bottom: 1px solid #a0a0a0;
      }
      
      .messageTable-Medium {
        background-color: #fffbbd;
        border-bottom: 1px solid #ededed;
      }
      
      .messageTable-Low {
        background-color: #d0ffbd;
        border-bottom: 1px solid #808080;
      }
      
      #errorDialogMessage {
        padding: 15px 15px;
        width: 300px;
        height: 100px;
        overflow: auto;
      }
    </style>
    <script type="text/javascript">
      var messageNames = [
        'importance', 'granuleName', 'state', 'lock', 'updated'
      ];
      var messageNameImportance = 'importance';
      var messageNameUpdated = 'updated';
      var loadIconContainer = 'loadIconContainer';
      var messageTableRows = 'messageTableRows';
      var messageTableColorName = 'messageTable';
      var messageTableRowName = 'messageTableRow';
      var messageTableColumnName = 'messageTableColumn';
      var messageTableName = 'messageTable';
      var messageTable = 'messageTable';
      var messageTableMessageName = 'messageTableMessage';
      var errorDialog = 'errorDialog';
      var errorDialogMessage = 'errorDialogMessage';
      
      var messageTableNavigatorNewest = 'messageTableNavigatorNewest';
      var messageTableNavigatorNewer = 'messageTableNavigatorNewer';
      var messageTableNavigatorOldest = 'messageTableNavigatorOldest';
      var messageTableNavigatorOlder = 'messageTableNavigatorOlder';
      var messageTableNavigatorFrom = 'messageTableNavigatorFrom';
      var messageTableNavigatorTo = 'messageTableNavigatorTo';
      var messageTableNavigatorTotal = 'messageTableNavigatorTotal';
      
      // paging
      var pagingPage = ${flash['paging.page']};
      var pagingPages = ${flash['paging.pages']};
      var pagingGranulesPerPage = ${flash['paging.granulesPerPage']};
      var pagingGranulesCount = ${flash['paging.granulesCount']};
      var pagingGranulesFrom = ${flash['paging.granulesFrom']};
      var pagingGranulesTo = ${flash['paging.granulesTo']};
      var pagingNewer = ${flash['paging.newer']};
      var pagingNewest = ${flash['paging.newest']};
      var pagingOlder = ${flash['paging.older']};
      var pagingOldest = ${flash['paging.oldest']};
      var pagingTargetPage = pagingPage;
    
      var messageReceivedSucceeded = function(items, request) {
        var messagesNode = dojo.byId(messageTableRows);
        while(messagesNode.firstChild) {
          messagesNode.removeChild(messagesNode.firstChild);
        }
        
        for(var i = 0; i < items.length; i++) {
          var item = items[i];
          //console.debug('type: '+item['type']);
          
          if(item['type'] == 'Paging') {
            //console.debug('coming!');
            
            pagingPage = item['page'];
            pagingPages = item['pages'];
            pagingGranulesPerPage = item['granulesPerPage'];
            pagingGranulesCount = item['granulesCount'];
            pagingGranulesFrom = item['granulesFrom'];
            pagingGranulesTo = item['granulesTo'];
            pagingNewer = item['newer'];
            pagingNewest = item['newest'];
            pagingOlder = item['older'];
            pagingOldest = item['oldest'];
            
            dojo.style(messageTableNavigatorNewest, 'visibility', ((pagingNewest != 'null') ? 'visible' : 'hidden'));
            dojo.style(messageTableNavigatorNewer, 'visibility', ((pagingNewer != 'null') ? 'visible' : 'hidden'));
            dojo.style(messageTableNavigatorOldest, 'visibility', ((pagingOldest != 'null') ? 'visible' : 'hidden'));
            dojo.style(messageTableNavigatorOlder, 'visibility', ((pagingOlder != 'null') ? 'visible' : 'hidden'));
            
            dojo.byId(messageTableNavigatorFrom).innerHTML = pagingGranulesFrom;
            dojo.byId(messageTableNavigatorTo).innerHTML = pagingGranulesTo;
            dojo.byId(messageTableNavigatorTotal).innerHTML = pagingGranulesCount;
          } else {
            //console.debug('hmm!');
            
            // all other status
            var newRowNode = dojo.doc.createElement('div');
            dojo.addClass(newRowNode, messageTableRowName);
            messagesNode.appendChild(newRowNode);

            for(var j = 0; j < messageNames.length; j++) {
              var messageName = messageNames[j];

              var newColumnNode = dojo.doc.createElement('div');
              
              // convert time in long to string representation
              var itemValue = item[messageName];
              if(messageName == messageNameUpdated) {
              	//console.debug('time: '+itemValue);
              	var date = new Date(itemValue);
              	
              	var month = date.getMonth() + 1;
              	if(month < 10) {
              		month = '0'+month;
              	}
              	var dayOfMonth = date.getDate();
              	var year = date.getFullYear();
              	var hour = date.getHours();
              	if(hour < 10) {
              		hour = '0'+hour;
              	}
              	var minute = date.getMinutes();
              	if(minute < 10) {
              		minute = '0'+minute;
              	}
              	var second = date.getSeconds();
              	if(second < 10) {
              		second = '0'+second;
              	}
              	var timeZone = dojo.date.getTimezoneName(new Date());
              	var dateRepresentation = month+'/'+dayOfMonth+'/'+year+' '+hour+':'+minute+':'+second+' '+timeZone;
              	//console.debug('date: '+dateRepresentation);
              	itemValue = dateRepresentation;
              }
              
              newColumnNode.innerHTML = itemValue;
              dojo.addClass(newColumnNode, messageTableColumnName+'-'+messageName);
              dojo.addClass(newColumnNode, messageTableName+'-'+item[messageNameImportance]);
              newRowNode.appendChild(newColumnNode);
            }

            // message
            var newMessageRowNode = dojo.doc.createElement('div');
            var newMessageRowNodeId = messageTableRowName+"-"+i;
            dojo.attr(newMessageRowNode, 'id', newMessageRowNodeId);
            dojo.addClass(newMessageRowNode, messageTableRowName);
            dojo.style(newMessageRowNode, 'display', 'none')
            dojo.connect(newRowNode, 'onclick', dojo.hitch(null, 'showElement', newMessageRowNodeId));
            messagesNode.appendChild(newMessageRowNode);

            var newMessageNode = dojo.doc.createElement('div');
            dojo.addClass(newMessageNode, messageTableMessageName);
            newMessageRowNode.appendChild(newMessageNode);

            var textBox = new dijit.form.TextBox({style: 'width: 100%;height: 50px;', readOnly: true});
            textBox.setValue(item['note']);
            newMessageNode.appendChild(textBox.domNode);

            var newLinkContainerNode = dojo.doc.createElement('div');
            dojo.style(newLinkContainerNode, 'width', '100%');
            dojo.style(newLinkContainerNode, 'text-align', 'right');
            newMessageNode.appendChild(newLinkContainerNode);

            /*
            var newLinkNode = dojo.doc.createElement('a');
            dojo.attr(newLinkNode, 'href', "${createLink(action: 'granuleStatusEdit')}"+"?id="+item['id']);
            newLinkNode.innerHTML = 'Edit';
            newLinkContainerNode.appendChild(newLinkNode);
            */
          }
        }
        
        var messageTableNode = dojo.byId(messageTable);
        dojo.style(messageTableNode, 'opacity', 0);
        dojo.fadeIn({node: messageTableNode, duration: 500}).play();
        
        updateGranuleStatusDone();
      }
      
      var messageReceivedFailed = function(arg1, arg2) {
        updateGranuleStatusDone();
        
        dojo.byId(errorDialogMessage).innerHTML = arg1.message;
        dijit.byId(errorDialog).show();
      }
      
      function updateGranuleStatus() {
        updateLoadingIcon(true);
        
        //console.debug('page: '+pagingTargetPage);
        commonReceiveMessage(
          commonDataSourceUrls['granuleStatus']+'?page='+pagingTargetPage,
          messageReceivedSucceeded,
          messageReceivedFailed
        );
      }
      
      function updateGranuleStatusDone() {
        updateLoadingIcon(false);
      }
      
      function updateLoadingIcon(isLoading) {
        var visibility = 'hidden';
        if(isLoading) {
          visibility = 'visible';
        }
        
        dojo.style(loadIconContainer, 'visibility', visibility);
      }
      
      function showElement(id) {
        //console.debug('id: '+id);
        var targetNode = dojo.byId(id);
        
        var display = 'block';
        if(dojo.style(targetNode, 'display').toLowerCase() != 'none') {
          display = 'none';
        }
        dojo.style(targetNode, 'display', display);
      }
      
      function loadPage(page) {
        pagingTargetPage = page;
        updateGranuleStatus();
      }
      
      dojo.addOnLoad(function() {
        updateGranuleStatus();
      });
    </script>
  </head>
  <body class="tundra">
    <div id="mainContainer">
      <div>
        <div dojoType="dijit.Dialog" id="errorDialog" title="Error">
          <div id="errorDialogMessage">
          </div>
        </div>
      </div>
      <div>
        <g:render template="/common/header" />
      </div>
      <div>
        <div id="loadContainer">
          <!--
          <div id="loadButtonContainer">
            <button dojoType="dijit.form.Button" onclick="updateGranuleStatus();">Update</button>
          </div>
          -->
          <div id="loadIconContainer">
            <img id="loadIcon" src="${createLinkTo(dir: 'images/monitor', file: 'Loading.gif')}" />
          </div>
        </div>
      </div>
      <!--
      <div>
        <div id="messageContainer">
        </div>
      </div>
      -->
      <div>
        <div id="messageTable">
          <div id="messageTableNavigator">
            <!--
            <div id="messageTableNavigatorNewest" onclick="loadPage(pagingNewest);">&lt;&lt;</div>
            <div id="messageTableNavigatorNewer" onclick="loadPage(pagingNewer);">&lt;</div>
            <div id="messageTableNavigatorFrom"></div>
            <div class="messageTableNavigatorText"> - </div>
            <div id="messageTableNavigatorTo"></div>
            <div class="messageTableNavigatorText"> of </div>
            <div id="messageTableNavigatorTotal"></div>
            <div id="messageTableNavigatorOlder" onclick="loadPage(pagingOlder);">&gt;</div>
            <div id="messageTableNavigatorOldest" onclick="loadPage(pagingOldest);">&gt;&gt;</div>
            -->
            <ul style="float: right; list-style-type: none; padding: 15px 15px; margin: 0px; margin-left: auto; margin-right: auto;">
              <li id="messageTableNavigatorNewest"><a href="#" onclick="loadPage(pagingNewest);">&lt;&lt;&#160;Newest</a></li>
              <li id="messageTableNavigatorNewer"><a href="#" onclick="loadPage(pagingNewer);">&lt;&#160;Newer</a></li>
              <li id="messageTableNavigatorFrom"></li>
              <li class="messageTableNavigatorText"> - </li>
              <li id="messageTableNavigatorTo"></li>
              <li class="messageTableNavigatorText"> of </li>
              <li id="messageTableNavigatorTotal"></li>
              <li id="messageTableNavigatorOlder"><a href="#" onclick="loadPage(pagingOlder);">Older&#160;&gt;</a></li>
              <li id="messageTableNavigatorOldest"><a href="#" onclick="loadPage(pagingOldest);">Oldest&#160;&gt;&gt;</a></li>
            </ul>
          </div>
          <div class="messageTableHeader">
            <div class="messageTableHeaderColumn-Importance">Importance</div>
            <div class="messageTableHeaderColumn-GranuleName">Granule Name</div>
            <div class="messageTableHeaderColumn-State">State</div>
            <div class="messageTableHeaderColumn-Lock">Lock</div>
            <div class="messageTableHeaderColumn-Updated">Updated</div>
          </div>
          <div id="messageTableRows">
          </div>
        </div>
      </div>
      <div>
        <g:render template="/common/footer" />
      </div>
    </div>
  </body>
</html>
