/** ***************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 **************************************************************************** */
/*
import java.util.TreeMap
import java.util.LinkedHashMap
import grails.converters.JSON

import gov.nasa.podaac.inventory.model.Dataset
import gov.nasa.podaac.inventory.model.DatasetLocationPolicy
import gov.nasa.podaac.inventory.model.Provider
import gov.nasa.podaac.inventory.model.DatasetPolicy
*/
/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: MonitorController.groovy 2766 2009-04-15 00:42:40Z axt $
 */
class MonitorController {
	/*
  def authenticationService
  def granuleStatusService
  def inventoryService
  
  def index = {
    redirect(action: 'login')
  }
    
  def login = {
    boolean loggedIn = authenticationService.isLoggedIn(session)
    
    if(!loggedIn) {
      String username = params['username']
      String password = params['password']
      if((username != null) && (password != null)) {
        IngSystemUser user = authenticationService.verifyUser(username, password)
        if(user) {
          authenticationService.logIn(session, user)
          loggedIn = true
        } else {
          flash['login.message'] = 'Username and password did not match.'
        }
      }
    }
    
    if(loggedIn) {
      redirect(action: 'granuleStatus')
    } else {
      render(view: 'login')
    }
  }
  
  def logout = {
    authenticationService.logOut(session)
    
    redirect(action: 'login')
  }
  
  def home = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    render(view: 'home')
  }
  
  def dataLatency = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    render(view: 'dataLatency')
  }
  
  def dataLatencyEdit = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    flash['dataset'] = new Dataset()
    flash['provider'] = new Provider()
    flash['providers'] = inventoryService.getProviders()
    Set<DatasetLocationPolicy> locationPolicySet = new LinkedHashSet<DatasetLocationPolicy>();
    locationPolicySet.add(new DatasetLocationPolicy());
    locationPolicySet.add(new DatasetLocationPolicy());
    flash['locationPolicySet'] = locationPolicySet
    flash['policy'] = new DatasetPolicy()
    if(params.id) {
      Dataset dataset = inventoryService.getDataset(Integer.parseInt(params.id))
      flash['dataset'] = dataset
      flash['provider'] = dataset.getProvider()
      flash['locationPolicySet'] = dataset.getLocationPolicySet()
      flash['policy'] = dataset.getDatasetPolicy()
    }
    
    render(view: 'dataLatencyEdit')
  }
  
  def granuleStatus = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    int page = 0
    if(params.page) {
      page = Integer.parseInt(params.page)
    }
    int numberOfGranules = granuleStatusService.getGranulesCount()
    
    int numberOfPages = (numberOfGranules / GranuleStatusController.GRANULES_PER_PAGE)
    if((numberOfGranules % GranuleStatusController.GRANULES_PER_PAGE) != 0) {
      numberOfPages += 1
    }
    
    flash['paging.page'] = page
    flash['paging.granulesPerPage'] = GranuleStatusController.GRANULES_PER_PAGE
    flash['paging.granulesCount'] = numberOfGranules
    flash['paging.granulesFrom'] = (page * GranuleStatusController.GRANULES_PER_PAGE) + 1
    flash['paging.granulesTo'] = (((page + 1) * GranuleStatusController.GRANULES_PER_PAGE) > numberOfGranules) ? numberOfGranules : ((page + 1) * GranuleStatusController.GRANULES_PER_PAGE)
    flash['paging.newer'] = (page > 0) ? (page - 1) : 'null'
    flash['paging.newest'] = (page > 1) ? 0 : 'null'
    flash['paging.older'] = ((page + 1) < numberOfPages) ? (page + 1) : 'null'
    flash['paging.oldest'] = ((page + 2) < numberOfPages) ? (numberOfPages - 1) : 'null'
    flash['paging.pages'] = numberOfPages
    
    render(view: 'granuleStatus')
  }
  
  def granuleStatusEdit = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    def product = granuleStatusService.getGranule(Integer.parseInt(params.id))
    flash['granule'] = product
    
    flash['optionsName'] = GranuleStatusController.GRANULE_UPDATE_OPTIONS_NAME
    flash['options'] = GranuleStatusController.GRANULE_UPDATE_OPTIONS
    
    render(view: 'granuleStatusEdit')
  }
  
  def storageStatus = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    render(view: 'storageStatus')
  }
  
  def provider = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    render(view: 'provider')
  }
  
  def providerEdit = {
    if(!authenticationService.isLoggedIn(session)) {
      redirect(action: 'login')
    }
    
    flash['provider'] = [:]
    String id = params.id
    if(id) {
      Map provider = inventoryService.getProvider(Integer.parseInt(id))
      flash['provider'] = provider
    }
    
    render(view: 'providerEdit')
  }
  */
}
