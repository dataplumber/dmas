import gov.nasa.jpl.horizon.api.Encrypt
import gov.nasa.jpl.horizon.api.Lock
import gov.nasa.jpl.horizon.api.State
import gov.nasa.podaac.common.api.zookeeper.api.constants.JobPriority
import gov.nasa.podaac.common.api.zookeeper.api.ZkAccess
import gov.nasa.podaac.common.api.zookeeper.api.ZkFactory
import grails.util.GrailsUtil
//import gov.nasa.podaac.inventory.hibernate.HibernateSessionFactory
import org.apache.zookeeper.WatchedEvent
import org.apache.zookeeper.Watcher

import org.codehaus.groovy.grails.commons.ConfigurationHolder

/*
* Copyright (c) 2008 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

/**
 * BootStrap setup class to setup the target operating environment
 *
 * @author T. Huang [Thomas.Huang@jpl.nasa.gov]
 * @version $Id: BootStrap.groovy 2686 2009-03-03 22:18:55Z axt $
 */
class BootStrap {
  private static final int ENGINE_JOBS_PER_PAGING = 10
  
  def quartzScheduler
  def storageService

  // supported product types
  def productTypes = [
          [name: 'GHRSST-ABOM-L4HRfnd-AUS-RAMSSA_09km', event: 'ABOM-L4HRfnd-AUS-RAMSSA_09km', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-ABOM-L4LRfnd-GLOB-GAMSSA_28km', event: 'ABOM-L4LRfnd-GLOB-GAMSSA_28km', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-REMSS-L2P-AMSRE', event: 'REMSS-L2P-AMSRE', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-REMSS-L2P_GRIDDED_25-AMSRE', event: 'REMSS-L2P_GRIDDED_25-AMSRE', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AMSRE', event: 'EUR-L2P-AMSRE', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-ATS_NR_2P', event: 'EUR-L2P-ATS_NR_2P', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AVHRR16_G', event: 'EUR-L2P-AVHRR16_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AVHRR16_L', event: 'EUR-L2P-AVHRR16_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AVHRR17_G', event: 'EUR-L2P-AVHRR17_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR17_G', event: 'NAVO-L2P-AVHRR17_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AVHRR17_L', event: 'EUR-L2P-AVHRR17_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR17_L', event: 'NAVO-L2P-AVHRR17_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NEODAAS-L2P-AVHRR17_L', event: 'NEODAAS-L2P-AVHRR17_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR18_G', event: 'NAVO-L2P-AVHRR18_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR18_L', event: 'NAVO-L2P-AVHRR18_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NEODAAS-L2P-AVHRR18_L', event: 'NEODAAS-L2P-AVHRR18_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRRMTA_G', event: 'NAVO-L2P-AVHRRMTA_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-DMI-L4UHfnd-NSEABALTIC-DMI_OI', event: 'DMI-L4UHfnd-NSEABALTIC-DMI_OI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L4HRfnd-GLOB-ODYSSEA', event: 'EUR-L4HRfnd-GLOB-ODYSSEA', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L4UHRfnd-GAL-ODYSSEA', event: 'EUR-L4UHRfnd-GAL-ODYSSEA', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L4UHRfnd-MED-ODYSSEA', event: 'EUR-L4UHRfnd-MED-ODYSSEA', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L4UHFnd-MED-v01', event: 'EUR-L4UHFnd-MED-v01', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L4UHRfnd-NWE-ODYSSEA', event: 'EUR-L4UHRfnd-NWE-ODYSSEA', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-OSDPD-L2P-GOES11', event: 'OSDPD-L2P-GOES11', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-OSDPD-L2P-GOES12', event: 'OSDPD-L2P-GOES12', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-JPL-L2P-MODIS_A', event: 'JPL-L2P-MODIS_A', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-JPL-L2P-MODIS_T', event: 'JPL-L2P-MODIS_T', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-NAR16_SST', event: 'EUR-L2P-NAR16_SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-NAR17_SST', event: 'EUR-L2P-NAR17_SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-NAR18_SST', event: 'EUR-L2P-NAR18_SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L4HR1m-GLOB-K10_SST', event: 'NAVO-L4HR1m-GLOB-K10_SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI', event: 'NCDC-L4LRblend-GLOB-AVHRR_AMSR_OI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NCDC-L4LRblend-GLOB-AVHRR_OI', event: 'NCDC-L4LRblend-GLOB-AVHRR_OI', prefix: 'GHRSST-', federation: 'podaacDev'],
          /*[name: 'GHRSST-REMSS-L4HRfnd-GLOB-amsre_OI', event: 'REMSS-L4HRfnd-GLOB-amsre_OI', prefix: 'GHRSST-', federation: 'podaacDev'],*/
          [name: 'GHRSST-REMSS-L4HRfnd-GLOB-mw_ir_OI', event: 'REMSS-L4HRfnd-GLOB-mw_ir_OI', prefix: 'GHRSST-', federation: 'podaacDev'],
          /*[name: 'GHRSST-REMSS-L4HRfnd-GLOB-tmi_OI', event: 'REMSS-L4HRfnd-GLOB-tmi_OI', prefix: 'GHRSST-', federation: 'podaacDev'],*/
          /*[name: 'GHRSST-REMSS-L4HRfnd-GLOB-tmi_amsre_OI', event: 'REMSS-L4HRfnd-GLOB-tmi_amsre_OI', prefix: 'GHRSST-', federation: 'podaacDev'],*/
          [name: 'GHRSST-EUR-L2P-SEVIRI_SST', event: 'EUR-L2P-SEVIRI_SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-REMSS-L2P-TMI', event: 'REMSS-L2P-TMI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-REMSS-L2P_GRIDDED_25-TMI', event: 'REMSS-L2P_GRIDDED_25-TMI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-TMI', event: 'EUR-L2P-TMI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-UKMO-L4HRfnd-GLOB-OSTIA', event: 'UKMO-L4HRfnd-GLOB-OSTIA', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-REMSS-L4HRfnd-GLOB-mw_ir_rt_OI', event: 'REMSS-L4HRfnd-GLOB-mw_ir_rt_OI', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'JASON-1_ATG_SSHA', event: 'DMAS', federation: 'podaacDev'],
          [name: 'JASON-1_GDR_NETCDF', event: 'DMAS', federation: 'podaacDev'],
          [name: 'JASON-1_GDR_SSHA_NETCDF', event: 'DMAS', federation: 'podaacDev'],
          [name: 'JASON-1_IGDR', event: 'JASON-1_IGDR', federation: 'podaacDev'],
          [name: 'JASON-1_IGDR_NETCDF', event: 'JASON-1_IGDR_NETCDF', federation: 'podaacDev'],
          [name: 'JASON-1_IGDR_SSHA_NETCDF', event: 'JASON-1_IGDR_SSHA_NETCDF', federation: 'podaacDev'],
          [name: 'JASON-1_OSDR',event: 'JASON-1_OSDR', federation: 'podaacDev'],
          [name: 'JASON-1_SGDR_NETCDF',event: 'DMAS', federation: 'podaacDev'],
          [name: 'JASON-1_SIGDR_NETCDF',event: 'JASON-1_SIGDR_NETCDF', federation: 'podaacDev'],
          [name: 'JASON-1_SSHA',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_ACS',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_Anc',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_FTS_Anc',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_FTS_Igram',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_FTS_Spectra',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_L1A',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_L1B',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_L2',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_L2_FullPhysics',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_MOC',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_RICA',event: 'DMAS', federation: 'podaacDev'],
          [name: 'OCO_Telem',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCAT0QA',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL1AQ',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATATT',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATCAL',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATICEM',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATEPHG',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATNWP1',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATNWP2',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATQARPT',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATREVTIME',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATTCD',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATQL0',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATHK1',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATHK2',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL0',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATBP',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL1A',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL1B',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL1BQ',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2A_PULS',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2AQ_PULS',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2A_CP12',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2AQ_CP12',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2B_PULS',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2BQ_PULS',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2B_CP12',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL2BQ_CP12',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCATL3',event: 'DMAS', federation: 'podaacDev'],
          [name: 'QSCAT_ANCILLARY', event: 'DMAS', federation: 'podaacDev'],
          [name: 'GHRSST-UPA-L2P-ATS_NR_2P',event: 'UPA-L2P-ATS_NR_2P', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'ASCAT-L2-12.5km',event: 'ASCAT-L2-12.5km', federation: 'podaacDev'],
          [name: 'ASCAT-L2-25km', event: 'ASCAT-L2-25km', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L2P-AVHRR_METOP_A', event: 'EUR-L2P-AVHRR_METOP_A', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR19_L', event: 'NAVO-L2P-AVHRR19_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NAVO-L2P-AVHRR19_G', event: 'NAVO-L2P-AVHRR19_G', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-OSDPD-L2P-MTSAT1R', event: 'OSDPD-L2P-MTSAT1R', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L3P-GLOB_AVHRR_METOP_A', event: 'EUR-L3P-GLOB_AVHRR_METOP_A', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-OSDPD-L2P-MSG02', event: 'OSDPD-L2P-MSG02', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-EUR-L3P-NAR_AVHRR_METOP_A', event: 'EUR-L3P-NAR_AVHRR_METOP_A', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-NEODAAS-L2P-AVHRR19_L', event: 'NEODAAS-L2P-AVHRR19_L', prefix: 'GHRSST-', federation: 'podaacDev'],
          /* new datasets from 2.1.1 */
          [name: 'GHRSST-JPL-L4UHfnd-NCAMERICA-MUR', event: 'JPL-L4UHfnd-NCAMERICA-MUR', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'GHRSST-JPL_OUROCEAN-L4UHfnd-GLOB-G1SST', event: 'JPL_OUROCEAN-L4UHfnd-GLOB-G1SST', prefix: 'GHRSST-', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_StressCurl_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_StressCurl_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_Uwind_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_Uwind_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_StressDiverge_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_StressDiverge_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_Speed_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_Speed_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_SpeedCubed_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_SpeedCubed_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_Stress_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_Stress_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_SpeedSquared_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_SpeedSquared_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_RelVort_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_RelVort_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_TauX_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_TauX_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_Vwind_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_Vwind_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_Diverge_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_Diverge_US_West_Coast', federation: 'podaacDev'],
          [name: 'QSCAT_Vanhoff_L3_OW_TauY_US_West_Coast', event: 'QSCAT_Vanhoff_L3_OW_TauY_US_West_Coast', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_ANNUAL', event: 'AQUARIUS_L3_SSS_SMI_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_8DAY', event: 'AQUARIUS_L3_SSS_BIN_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_DAILY', event: 'AQUARIUS_L3_SSS_SMI_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_3MONTH', event: 'AQUARIUS_L3_SSS_SMI_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_8DAY', event: 'AQUARIUS_L3_WIND_SPEED_SMI_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY', event: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_ANNUAL', event: 'AQUARIUS_L3_SSS_BIN_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY', event: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_MONTHLY', event: 'AQUARIUS_L3_SSS_BIN_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L2_SSS', event: 'AQUARIUS_L2_SSS', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH', event: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_8DAY', event: 'AQUARIUS_L3_SSS_SMI_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_3MONTH', event: 'AQUARIUS_L3_SSS_BIN_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_ANNUAL', event: 'AQUARIUS_L3_WIND_SPEED_SMI_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_DAILY', event: 'AQUARIUS_L3_SSS_BIN_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_MONTHLY', event: 'AQUARIUS_L3_SSS_SMI_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L1A_SSS', event: 'AQUARIUS_L1A_SSS', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_LITE', event: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_FULL', event: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_IMAGES', event: 'QSCAT_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', event: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', event: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', event: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', event: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', event: 'QSCAT_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', event: 'QSCAT_BYU_L3_OW_SIGMA0_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          /* new dataset from 2.2.0 */
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F10_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F10_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F8_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F8_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F11_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F11_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F13_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F13_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F14_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F14_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F15_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMI_F15_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_TMI_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_TMI_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_AMSRE_WIND_VECTORS_FLK', event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_AMSRE_WIND_VECTORS_FLK', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L1A_SSS', event: 'AQUARIUS_SIM_L1A_SSS', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L2_SSS', event: 'AQUARIUS_SIM_L2_SSS', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_DAILY', event: 'AQUARIUS_SIM_L3_SSS_BIN_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_8DAY', event: 'AQUARIUS_SIM_L3_SSS_BIN_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_MONTHLY', event: 'AQUARIUS_SIM_L3_SSS_BIN_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_3MONTH', event: 'AQUARIUS_SIM_L3_SSS_BIN_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_ANNUAL', event: 'AQUARIUS_SIM_L3_SSS_BIN_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_DAILY', event: 'AQUARIUS_SIM_L3_SSS_SMI_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_8DAY', event: 'AQUARIUS_SIM_L3_SSS_SMI_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_MONTHLY', event: 'AQUARIUS_SIM_L3_SSS_SMI_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_3MONTH', event: 'AQUARIUS_SIM_L3_SSS_SMI_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_ANNUAL', event: 'AQUARIUS_SIM_L3_SSS_SMI_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_ANNUAL', event: 'AQUARIUS_SIM_L3_SSS_BIN_ANNUAL', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_DAILY', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_DAILY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_8DAY', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_8DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_MONTHLY', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_MONTHLY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_3MONTH', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_3MONTH', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_ANNUAL', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_ANNUAL', federation: 'podaacDev'],
          [name: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V2', event: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V2', federation: 'podaacDev'],
          [name: 'OSDPD-L2P-GOES13', event: 'OSDPD-L2P-GOES13', federation: 'podaacDev'],
          [name: 'ALT_TIDE_GAUGE_L4_OST_SLA_US_WEST_COAST', event: 'ALT_TIDE_GAUGE_L4_OST_SLA_US_WEST_COAST', federation: 'podaacDev'],
          [name: 'MERGED_TP_J1_OSTM_OST_CYCLES', event: 'MERGED_TP_J1_OSTM_OST_CYCLES', federation: 'podaacDev'],
          [name: 'MERGED_TP_J1_OSTM_OST_ALL', event: 'MERGED_TP_J1_OSTM_OST_ALL', federation: 'podaacDev'],
          [name: 'TOPEX_RGDR_V2010', event: 'TOPEX_RGDR_V2010', federation: 'podaacDev'],
          [name: 'TOPEX_RGDR_V2010_NETCDF', event: 'TOPEX_RGDR_V2010_NETCDF', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_IMAGES', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_FULL', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_LITE', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_RECTANGULAR_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_ARCTIC_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          [name: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Ad', event: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Ad', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_IMAGES', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_FULL', federation: 'podaacDev'],
          [name: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', event: 'SEAWINDS_BYU_L3_OW_SIGMA0_GLOBAL_ANTARCTICA_POLAR-STEREOGRAPHIC_BROWSE_MAPS_LITE', federation: 'podaacDev'],
          /* added from 3.0.0 */
          [name: 'JASON-1_JMR_ENH', event: 'JASON-1_JMR_ENH', federation: 'podaacDev'],
          [name: 'QSCAT_OSUCOAS_L3_OW_USWestCoast', event: 'QSCAT_OSUCOAS_L3_OW_USWestCoast', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L0_SSS', event: 'AQUARIUS_SIM_L0_SSS', federation: 'podaacDev'],
          [name: 'AQUARIUS_L0_SSS', event: 'AQUARIUS_L0_SSS', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_BIN_7DAY', event: 'AQUARIUS_L3_SSS_BIN_7DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_SSS_SMI_7DAY', event: 'AQUARIUS_L3_SSS_SMI_7DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY', event: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_BIN_7DAY', event: 'AQUARIUS_SIM_L3_SSS_BIN_7DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_SSS_SMI_7DAY', event: 'AQUARIUS_SIM_L3_SSS_SMI_7DAY', federation: 'podaacDev'],
          [name: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_7DAY', event: 'AQUARIUS_SIM_L3_WIND_SPEED_SMI_7DAY', federation: 'podaacDev'],
          /* added from 3.1.0 */
          [name: 'MODIS_TERRA_L3_SST_MID-IR_BINNED', event: 'MODIS_TERRA_L3_SST_MID-IR_BINNED', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_BINNED', event: 'MODIS_TERRA_L3_SST_THERMAL_BINNED', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_4KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_9KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_8DAY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_4KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_9KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_ANNUAL_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_4KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_9KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_DAILY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_4KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_4KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_9KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_8DAY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_4KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_9KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_ANNUAL_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_4KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_9KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_DAILY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_4KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_4KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_9KM_DAYTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_THERMAL_MONTHLY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_9KM_DAYTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_9KM_DAYTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_THERMAL_MONTHLY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_8DAY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_8DAY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_ANNUAL_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_ANNUAL_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_ANNUAL_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_ANNUAL_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_MONTHLY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_MONTHLY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_DAILY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_DAILY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_DAILY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_DAILY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_MONTHLY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_MONTHLY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_MONTHLY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_MONTHLY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_ANNUAL_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_ANNUAL_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_8DAY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_8DAY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_DAILY_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_DAILY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_MONTHLY_9KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_MONTHLY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_8DAY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_8DAY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_DAILY_9KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_DAILY_9KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_TERRA_L3_SST_MID-IR_8DAY_4KM_NIGHTTIME', event: 'MODIS_TERRA_L3_SST_MID-IR_8DAY_4KM_NIGHTTIME', federation: 'podaacDev'],
          [name: 'MODIS_AQUA_L3_SST_MID-IR_ANNUAL_4KM_NIGHTTIME', event: 'MODIS_AQUA_L3_SST_MID-IR_ANNUAL_4KM_NIGHTTIME', federation: 'podaacDev'],
		  /* added from 3.1.1 */
		  [name: 'QSCAT_LEVEL_2B_OWV_COMP_12', event: 'QSCAT_LEVEL_2B_OWV_COMP_12', federation: 'podaacDev'],
		  [name: 'QSCAT_LEVEL_2C_SFOWSV_COMP_12', event: 'QSCAT_LEVEL_2C_SFOWSV_COMP_12', federation: 'podaacDev'],
		  [name: 'QSCAT_LEVEL_2B_OWV_COMP_12_FULL', event: 'QSCAT_LEVEL_2B_OWV_COMP_12_FULL', federation: 'podaacDev'],
		  [name: 'QSCAT_LEVEL_2C_SFOWSV_COMP_12_FULL', event: 'QSCAT_LEVEL_2C_SFOWSV_COMP_12_FULL', federation: 'podaacDev'],
		  [name: 'ASCAT-L2-Coastal', event: 'ASCAT-L2-Coastal', federation: 'podaacDev'],
		  /* added from 3.2.0 */
		  [name: 'JASON-1_TRSR1280',event: 'JASON-1_TRSR1280', federation: 'podaacDev'],
		  [name: 'JASON-1_AUX', event: 'JASON-1_AUX', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_CNES', event: 'JASON-1_GDR_CNES', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_NASA', event: 'JASON-1_GDR_NASA', federation: 'podaacDev'],
		  [name: 'JASON-1_JMR', event: 'JASON-1_JMR', federation: 'podaacDev'],
		  [name: 'JASON-1_PLTM',event: 'JASON-1_PLTM', federation: 'podaacDev'],
		  [name: 'JASON-1_SGDR',event: 'JASON-1_SGDR', federation: 'podaacDev'],
		  [name: 'JASON-1_TRSR',event: 'JASON-1_TRSR', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_NETCDF_C_CNES',event: 'JASON-1_GDR_NETCDF_C_CNES', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_NETCDF_C_NASA',event: 'JASON-1_GDR_NETCDF_C_NASA', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_SSHA_NETCDF_CNES',event: 'JASON-1_GDR_SSHA_NETCDF_CNES', federation: 'podaacDev'],
		  [name: 'JASON-1_GDR_SSHA_NETCDF_NASA',event: 'JASON-1_GDR_SSHA_NETCDF_NASA', federation: 'podaacDev'],
		  [name: 'JASON-1_ECHO',event: 'JASON-1_ECHO', federation: 'podaacDev'],
		  [name: 'JASON-1_SGDR_C_NETCDF_CNES',event: 'JASON-1_SGDR_C_NETCDF_CNES', federation: 'podaacDev'],
		  [name: 'JASON-1_SGDR_C_NETCDF_NASA',event: 'JASON-1_SGDR_C_NETCDF_NASA', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA',event: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES',event: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES', federation: 'podaacDev'],
        /* added from 3.2.1 */
        [name: 'JPL-L4UHblend-NCAMERICA-RTO_SST_An',event: 'JPL-L4UHblend-NCAMERICA-RTO_SST_An', federation: 'podaacDev'],
        [name: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Td',event: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Td', federation: 'podaacDev'],
        [name: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Tn',event: 'JPL-L4UHblend-NCAMERICA-RTO_SST_Tn', federation: 'podaacDev'],
        [name: 'JPL-L4UHfnd-GLOB-MUR',event: 'JPL-L4UHfnd-GLOB-MUR', federation: 'podaacDev'],
        [name: 'OSDPD-L2P-MTSAT2',event: 'OSDPD-L2P-MTSAT2', federation: 'podaacDev'],
        [name: 'JASON-1_LOD',event: 'JASON-1_LOD', federation: 'podaacDev'],
        [name: 'EUR-L3P-NAR_AVHRR_NOAA_19',event: 'EUR-L3P-NAR_AVHRR_NOAA_19', federation: 'podaacDev'],
        /* added from 4.0.0 */
        [name: 'JASON-1_L2_OST_NRTSSHA',event: 'JASON-1_L2_OST_NRTSSHA', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SSHA_Ver-C',event: 'JASON-1_L2_OST_SSHA_Ver-C', federation: 'podaacDev'],
        [name: 'WINDSAT_NRL_L0_OW_RAW',event: 'WINDSAT_NRL_L0_OW_RAW', federation: 'podaacDev'],
        [name: 'WINDSAT_NRL_OW_LOD_FILES',event: 'WINDSAT_NRL_OW_LOD_FILES', federation: 'podaacDev'],
        [name: 'WINDSAT_NRL_L2_OW_EDR_WIND_VECTORS_NC_V2XX',event: 'WINDSAT_NRL_L2_OW_EDR_WIND_VECTORS_NC_V2XX', federation: 'podaacDev'],
        [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMIS_F17_WIND_VECTORS_FLK',event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_SSMIS_F17_WIND_VECTORS_FLK', federation: 'podaacDev'],
        [name: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_WINDSAT_WIND_VECTORS_FLK',event: 'CCMP_MEASURES_ATLAS_L3_OW_L2_5_WINDSAT_WIND_VECTORS_FLK', federation: 'podaacDev'],
        [name: 'CCMP_MEASURES_ATLAS_L4_OW_L3_0_WIND_VECTORS_FLK',event: 'CCMP_MEASURES_ATLAS_L4_OW_L3_0_WIND_VECTORS_FLK', federation: 'podaacDev'],
        [name: 'CCMP_MEASURES_ATLAS_L4_OW_L3_5A_MONTHLY_WIND_VECTORS_FLK',event: 'CCMP_MEASURES_ATLAS_L4_OW_L3_5A_MONTHLY_WIND_VECTORS_FLK', federation: 'podaacDev'],
        [name: 'CCMP_MEASURES_ATLAS_L4_OW_L3_5A_5DAY_WIND_VECTORS_FLK',event: 'CCMP_MEASURES_ATLAS_L4_OW_L3_5A_5DAY_WIND_VECTORS_FLK', federation: 'podaacDev'],
        [name: 'AVISO_L4_DYN_TOPO_1DEG_1MO',event: 'AVISO_L4_DYN_TOPO_1DEG_1MO', federation: 'podaacDev'],
        [name: 'AVISO_L4_DYN_TOPO_1DEG_1MO_NOB',event: 'AVISO_L4_DYN_TOPO_1DEG_1MO_NOB', federation: 'podaacDev'],
        [name: 'AVISO_L4_DYN_TOPO_1DEG_1MO_ERR',event: 'AVISO_L4_DYN_TOPO_1DEG_1MO_ERR', federation: 'podaacDev'],
        [name: 'AMSRE_L3_SST_1DEG_1MO',event: 'AMSRE_L3_SST_1DEG_1MO', federation: 'podaacDev'],
        [name: 'AMSRE_L3_SST_1DEG_1MO_ERR',event: 'AMSRE_L3_SST_1DEG_1MO_ERR', federation: 'podaacDev'],
        [name: 'AMSRE_L3_SST_1DEG_1MO_NOBS',event: 'AMSRE_L3_SST_1DEG_1MO_NOBS', federation: 'podaacDev'],
        [name: 'QSCAT_ARCTIC_MULTIYEAR_SEA_ICE_FRACTION_KWOK', event: 'QSCAT_ARCTIC_MULTIYEAR_SEA_ICE_FRACTION_KWOK', federation: 'podaacDev'],
        [name: 'REMSS-L2P_GRIDDED_25-WSAT',event: 'REMSS-L2P_GRIDDED_25-WSAT', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2_SSS_V11',event: 'AQUARIUS_L2_SSS_V11', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2_SSS_V12',event: 'AQUARIUS_L2_SSS_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_DAILY_V12',event: 'AQUARIUS_L3_SSS_BIN_DAILY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_DAILY_V12',event: 'AQUARIUS_L3_SSS_SMI_DAILY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY_V12',event: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_7DAY_V12',event: 'AQUARIUS_L3_SSS_BIN_7DAY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_7DAY_V12',event: 'AQUARIUS_L3_SSS_SMI_7DAY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY_V12',event: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_MONTHLY_V12',event: 'AQUARIUS_L3_SSS_BIN_MONTHLY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_MONTHLY_V12',event: 'AQUARIUS_L3_SSS_SMI_MONTHLY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY_V12',event: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_3MONTH_V12',event: 'AQUARIUS_L3_SSS_BIN_3MONTH_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_3MONTH_V12',event: 'AQUARIUS_L3_SSS_SMI_3MONTH_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH_V12',event: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH_V12', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2_SSS_V12DR',event: 'AQUARIUS_L2_SSS_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_DAILY_V12DR',event: 'AQUARIUS_L3_SSS_BIN_DAILY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_DAILY_V12DR',event: 'AQUARIUS_L3_SSS_SMI_DAILY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY_V12DR',event: 'AQUARIUS_L3_WIND_SPEED_SMI_DAILY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_7DAY_V12DR',event: 'AQUARIUS_L3_SSS_BIN_7DAY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_7DAY_V12DR',event: 'AQUARIUS_L3_SSS_SMI_7DAY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY_V12DR',event: 'AQUARIUS_L3_WIND_SPEED_SMI_7DAY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_MONTHLY_V12DR',event: 'AQUARIUS_L3_SSS_BIN_MONTHLY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_MONTHLY_V12DR',event: 'AQUARIUS_L3_SSS_SMI_MONTHLY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY_V12DR',event: 'AQUARIUS_L3_WIND_SPEED_SMI_MONTHLY_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_BIN_3MONTH_V12DR',event: 'AQUARIUS_L3_SSS_BIN_3MONTH_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_SMI_3MONTH_V12DR',event: 'AQUARIUS_L3_SSS_SMI_3MONTH_V12DR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH_V12DR',event: 'AQUARIUS_L3_WIND_SPEED_SMI_3MONTH_V12DR', federation: 'podaacDev'],
        /* added from 4.1.0 */
        [name: 'GRACE_AOD1B_GRAV_GFZ_RL01',event: 'GRACE_AOD1B_GRAV_GFZ_RL01', federation: 'podaacDev'],
        [name: 'GRACE_AOD1B_GRAV_GFZ_RL03',event: 'GRACE_AOD1B_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_AOD1B_GRAV_GFZ_RL04',event: 'GRACE_AOD1B_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_AOD1B_GRAV_GFZ_RL05',event: 'GRACE_AOD1B_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_FLINN_GRAV_JPL',event: 'GRACE_FLINN_GRAV_JPL', federation: 'podaacDev'],
        [name: 'GRACE_FLINNR_GRAV_JPL',event: 'GRACE_FLINNR_GRAV_JPL', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_GFZ_RL02',event: 'GRACE_GAA_L2_GRAV_GFZ_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_GFZ_RL03',event: 'GRACE_GAA_L2_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_GFZ_RL04',event: 'GRACE_GAA_L2_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_GFZ_RL05',event: 'GRACE_GAA_L2_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_JPL_RL02',event: 'GRACE_GAA_L2_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_JPL_RL04',event: 'GRACE_GAA_L2_GRAV_JPL_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_JPL_RL04_1',event: 'GRACE_GAA_L2_GRAV_JPL_RL04_1', federation: 'podaacDev'],
        [name: 'GRACE_GAA_L2_GRAV_JPL_RL05',event: 'GRACE_GAA_L2_GRAV_JPL_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_GFZ_RL02',event: 'GRACE_GAB_L2_GRAV_GFZ_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_GFZ_RL03',event: 'GRACE_GAB_L2_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_GFZ_RL04',event: 'GRACE_GAB_L2_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_GFZ_RL05',event: 'GRACE_GAB_L2_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_JPL_RL02',event: 'GRACE_GAB_L2_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_JPL_RL04',event: 'GRACE_GAB_L2_GRAV_JPL_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_JPL_RL04_1',event: 'GRACE_GAB_L2_GRAV_JPL_RL04_1', federation: 'podaacDev'],
        [name: 'GRACE_GAB_L2_GRAV_JPL_RL05',event: 'GRACE_GAB_L2_GRAV_JPL_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_CSR_RL01',event: 'GRACE_GAC_L2_GRAV_CSR_RL01', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_CSR_RL02',event: 'GRACE_GAC_L2_GRAV_CSR_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_CSR_RL04',event: 'GRACE_GAC_L2_GRAV_CSR_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_CSR_RL05',event: 'GRACE_GAC_L2_GRAV_CSR_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_GFZ_RL02',event: 'GRACE_GAC_L2_GRAV_GFZ_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_GFZ_RL03',event: 'GRACE_GAC_L2_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_GFZ_RL04',event: 'GRACE_GAC_L2_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_GFZ_RL05',event: 'GRACE_GAC_L2_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_JPL_RL02',event: 'GRACE_GAC_L2_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_JPL_RL04',event: 'GRACE_GAC_L2_GRAV_JPL_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_JPL_RL04_1',event: 'GRACE_GAC_L2_GRAV_JPL_RL04_1', federation: 'podaacDev'],
        [name: 'GRACE_GAC_L2_GRAV_JPL_RL05',event: 'GRACE_GAC_L2_GRAV_JPL_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_CSR_RL04',event: 'GRACE_GAD_L2_GRAV_CSR_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_CSR_RL05',event: 'GRACE_GAD_L2_GRAV_CSR_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_GFZ_RL04',event: 'GRACE_GAD_L2_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_GFZ_RL05',event: 'GRACE_GAD_L2_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_JPL_RL04',event: 'GRACE_GAD_L2_GRAV_JPL_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_JPL_RL04_1',event: 'GRACE_GAD_L2_GRAV_JPL_RL04_1', federation: 'podaacDev'],
        [name: 'GRACE_GAD_L2_GRAV_JPL_RL05',event: 'GRACE_GAD_L2_GRAV_JPL_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_CSR_RL01',event: 'GRACE_GSM_L2_GRAV_CSR_RL01', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_CSR_RL02',event: 'GRACE_GSM_L2_GRAV_CSR_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_CSR_RL04',event: 'GRACE_GSM_L2_GRAV_CSR_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_CSR_RL05',event: 'GRACE_GSM_L2_GRAV_CSR_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL02',event: 'GRACE_GSM_L2_GRAV_GFZ_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL03',event: 'GRACE_GSM_L2_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL04',event: 'GRACE_GSM_L2_GRAV_GFZ_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL04_UNCON',event: 'GRACE_GSM_L2_GRAV_GFZ_RL04_UNCON', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL05',event: 'GRACE_GSM_L2_GRAV_GFZ_RL05', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_GFZ_RL05_UNCON',event: 'GRACE_GSM_L2_GRAV_GFZ_RL05_UNCON', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_JPL_RL01',event: 'GRACE_GSM_L2_GRAV_JPL_RL01', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_JPL_RL02',event: 'GRACE_GSM_L2_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_JPL_RL04',event: 'GRACE_GSM_L2_GRAV_JPL_RL04', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_JPL_RL04_1',event: 'GRACE_GSM_L2_GRAV_JPL_RL04_1', federation: 'podaacDev'],
        [name: 'GRACE_GSM_L2_GRAV_JPL_RL05',event: 'GRACE_GSM_L2_GRAV_JPL_RL05', federation: 'podaacDev'],
        [name: 'GRACE_L0_GRAV',event: 'GRACE_L0_GRAV', federation: 'podaacDev'],
        [name: 'GRACE_L1A_GRAV_JPL_RL00',event: 'GRACE_L1A_GRAV_JPL_RL00', federation: 'podaacDev'],
        [name: 'GRACE_L1A_GRAV_JPL_RL01',event: 'GRACE_L1A_GRAV_JPL_RL01', federation: 'podaacDev'],
        [name: 'GRACE_L1A_GRAV_JPL_RL02',event: 'GRACE_L1A_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_L1B_GRAV_JPL_RL00',event: 'GRACE_L1B_GRAV_JPL_RL00', federation: 'podaacDev'],
        [name: 'GRACE_L1B_GRAV_JPL_RL01',event: 'GRACE_L1B_GRAV_JPL_RL01', federation: 'podaacDev'],
        [name: 'GRACE_L1B_GRAV_JPL_RL02',event: 'GRACE_L1B_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_CSR_RL00',event: 'GRACE_MEAN_L2_GRAV_CSR_RL00', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_CSR_RL01',event: 'GRACE_MEAN_L2_GRAV_CSR_RL01', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_CSR_RL04',event: 'GRACE_MEAN_L2_GRAV_CSR_RL04', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_CSR_RL05',event: 'GRACE_MEAN_L2_GRAV_CSR_RL05', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_GFZ_RL03',event: 'GRACE_MEAN_L2_GRAV_GFZ_RL03', federation: 'podaacDev'],
        [name: 'GRACE_MEAN_L2_GRAV_JPL_RL02',event: 'GRACE_MEAN_L2_GRAV_JPL_RL02', federation: 'podaacDev'],
        [name: 'GRACE_OCN1B_GRAV_GFZ_RL00',event: 'GRACE_OCN1B_GRAV_GFZ_RL00', federation: 'podaacDev'],
        [name: 'OSCAR_L4_OC_1deg',event: 'OSCAR_L4_OC_1deg', federation: 'podaacDev'],
        [name: 'OSCAR_L4_OC_third-deg',event: 'OSCAR_L4_OC_third-deg', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L2_2km_MCSST_FRAC',event: 'AVHRR_NAVOCEANO_L2_2km_MCSST_FRAC', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L2_2KM_MCSST_LAC',event: 'AVHRR_NAVOCEANO_L2_2KM_MCSST_LAC', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L2_9KM_MCSST_GAC',event: 'AVHRR_NAVOCEANO_L2_9KM_MCSST_GAC', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L3_18km_MCSST_DAYTIME',event: 'AVHRR_NAVOCEANO_L3_18km_MCSST_DAYTIME', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L3_18km_MCSST_NIGHTTIME',event: 'AVHRR_NAVOCEANO_L3_18km_MCSST_NIGHTTIME', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L3_MCSST_GAC',event: 'AVHRR_NAVOCEANO_L3_MCSST_GAC', federation: 'podaacDev'],
        [name: 'AVHRR_NAVOCEANO_L3_MCSST_LAC',event: 'AVHRR_NAVOCEANO_L3_MCSST_LAC', federation: 'podaacDev'],
        [name: 'ECMWF_L4_FORECAST_FIELDS',event: 'ECMWF_L4_FORECAST_FIELDS', federation: 'podaacDev'],
        [name: 'AQUARIUS_LOD',event: 'AQUARIUS_LOD', federation: 'podaacDev'],
        /* added from 4.2.0 */
        [name: 'TELLUS_1_DEG_COEF',event: 'TELLUS_1_DEG_COEF', federation: 'podaacDev'],
        [name: 'TELLUS_2_DEG_COEF',event: 'TELLUS_2_DEG_COEF', federation: 'podaacDev'],
        [name: 'TELLUS_DEG_5_COEF',event: 'TELLUS_DEG_5_COEF', federation: 'podaacDev'],
        [name: 'TELLUS_DOT_2008_DATA',event: 'TELLUS_DOT_2008_DATA', federation: 'podaacDev'],
        [name: 'TELLUS_DOT_2008_GIF',event: 'TELLUS_DOT_2008_GIF', federation: 'podaacDev'],
        [name: 'TELLUS_DOT_2008_JPG',event: 'TELLUS_DOT_2008_JPG', federation: 'podaacDev'],
        [name: 'TELLUS_ECCO_OBP',event: 'TELLUS_ECCO_OBP', federation: 'podaacDev'],
        [name: 'TELLUS_ECCO_OBP_KF080',event: 'TELLUS_ECCO_OBP_KF080', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_ANIM',event: 'TELLUS_GLDAS_MONTHLY_ANIM', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_GIF',event: 'TELLUS_GLDAS_MONTHLY_GIF', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_NC',event: 'TELLUS_GLDAS_MONTHLY_NC', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_NC_COR',event: 'TELLUS_GLDAS_MONTHLY_NC_COR', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_TXT',event: 'TELLUS_GLDAS_MONTHLY_TXT', federation: 'podaacDev'],
        [name: 'TELLUS_GLDAS_MONTHLY_TXT_COR',event: 'TELLUS_GLDAS_MONTHLY_TXT_COR', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_ANIM',event: 'TELLUS_LAND_ANIM', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_ANIM_RL05',event: 'TELLUS_LAND_ANIM_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_GIF',event: 'TELLUS_LAND_GIF', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_GIF_RL05',event: 'TELLUS_LAND_GIF_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_GTIF',event: 'TELLUS_LAND_GTIF', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_GTIF_RL05',event: 'TELLUS_LAND_GTIF_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_NC',event: 'TELLUS_LAND_NC', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_NC_RL05',event: 'TELLUS_LAND_NC_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_TXT',event: 'TELLUS_LAND_TXT', federation: 'podaacDev'],
        [name: 'TELLUS_LAND_TXT_RL05',event: 'TELLUS_LAND_TXT_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_ANIM',event: 'TELLUS_OCEANEOF_ANIM', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_ANIM_JPL_ERROR',event: 'TELLUS_OCEANEOF_ANIM_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_ANIM_RL05',event: 'TELLUS_OCEANEOF_ANIM_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_CSR',event: 'TELLUS_OCEANEOF_GIF_CSR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_CSR_RL05',event: 'TELLUS_OCEANEOF_GIF_CSR_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_GFZ',event: 'TELLUS_OCEANEOF_GIF_GFZ', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_GFZ_RL05',event: 'TELLUS_OCEANEOF_GIF_GFZ_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_JPL',event: 'TELLUS_OCEANEOF_GIF_JPL', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_JPL_ERR',event: 'TELLUS_OCEANEOF_GIF_JPL_ERR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GIF_JPL_RL05',event: 'TELLUS_OCEANEOF_GIF_JPL_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GTIF',event: 'TELLUS_OCEANEOF_GTIF', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GTIF_ERROR',event: 'TELLUS_OCEANEOF_GTIF_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_GTIF_RL05',event: 'TELLUS_OCEANEOF_GTIF_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_NC',event: 'TELLUS_OCEANEOF_NC', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_NC_JPL_ERROF',event: 'TELLUS_OCEANEOF_NC_JPL_ERROF', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_NC_RL05',event: 'TELLUS_OCEANEOF_NC_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_TXT',event: 'TELLUS_OCEANEOF_TXT', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_TXT_JPL_ERROR',event: 'TELLUS_OCEANEOF_TXT_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEANEOF_TXT_RL05',event: 'TELLUS_OCEANEOF_TXT_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_ANIM',event: 'TELLUS_OCEAN_ANIM', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_ANIM_JPL_ERROR',event: 'TELLUS_OCEAN_ANIM_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_ANIM_RL05',event: 'TELLUS_OCEAN_ANIM_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_CSR',event: 'TELLUS_OCEAN_GIF_CSR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_CSR_RL05',event: 'TELLUS_OCEAN_GIF_CSR_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_GFZ',event: 'TELLUS_OCEAN_GIF_GFZ', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_GFZ_RL05',event: 'TELLUS_OCEAN_GIF_GFZ_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_JPL',event: 'TELLUS_OCEAN_GIF_JPL', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_JPL_ERROR',event: 'TELLUS_OCEAN_GIF_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GIF_JPL_RL05',event: 'TELLUS_OCEAN_GIF_JPL_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GTIF',event: 'TELLUS_OCEAN_GTIF', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GTIF_JPL_ERROR',event: 'TELLUS_OCEAN_GTIF_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_GTIF_RL05',event: 'TELLUS_OCEAN_GTIF_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_NC',event: 'TELLUS_OCEAN_NC', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_NC_JPL_ERROR',event: 'TELLUS_OCEAN_NC_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_NC_RL05',event: 'TELLUS_OCEAN_NC_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_TXT',event: 'TELLUS_OCEAN_TXT', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_TXT_JPL_ERROR',event: 'TELLUS_OCEAN_TXT_JPL_ERROR', federation: 'podaacDev'],
        [name: 'TELLUS_OCEAN_TXT_RL05',event: 'TELLUS_OCEAN_TXT_RL05', federation: 'podaacDev'],
        [name: 'TELLUS_PGR_PDF',event: 'TELLUS_PGR_PDF', federation: 'podaacDev'],
        [name: 'TELLUS_PGR_PNG',event: 'TELLUS_PGR_PNG', federation: 'podaacDev'],
        [name: 'TELLUS_PGR_TXT',event: 'TELLUS_PGR_TXT', federation: 'podaacDev'],
        [name: 'JASON-1_AUX_GEODETIC',event: 'JASON-1_AUX_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_ECHO_GEODETIC',event: 'JASON-1_ECHO_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_CNES_C_GEODETIC',event: 'JASON-1_GDR_CNES_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_NASA_C_GEODETIC',event: 'JASON-1_GDR_NASA_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_NETCDF_C_CNES_GEODETIC',event: 'JASON-1_GDR_NETCDF_C_CNES_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_NETCDF_C_GEODETIC',event: 'JASON-1_GDR_NETCDF_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_NETCDF_C_NASA_GEODETIC',event: 'JASON-1_GDR_NETCDF_C_NASA_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_SSHA_NETCDF_CNES_C_GEODETIC',event: 'JASON-1_GDR_SSHA_NETCDF_CNES_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_SSHA_NETCDF_C_GEODETIC',event: 'JASON-1_GDR_SSHA_NETCDF_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_GDR_SSHA_NETCDF_NASA_C_GEODETIC',event: 'JASON-1_GDR_SSHA_NETCDF_NASA_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_IGDR_GEODETIC',event: 'JASON-1_IGDR_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_IGDR_NETCDF_GEODETIC',event: 'JASON-1_IGDR_NETCDF_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_IGDR_SSHA_NETCDF_GEODETIC',event: 'JASON-1_IGDR_SSHA_NETCDF_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_JMR_A_B_GEODETIC',event: 'JASON-1_JMR_A_B_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_JMR_GEODETIC',event: 'JASON-1_JMR_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_GDR_VER-C_BINARY_GEODETIC',event: 'JASON-1_L2_OST_GDR_VER-C_BINARY_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_NRTSSHA_GEODETIC',event: 'JASON-1_L2_OST_NRTSSHA_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES_GEODETIC',event: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_GEODETIC',event: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA_GEODETIC',event: 'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_LOD_GEODETIC',event: 'JASON-1_LOD_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_OSDR_GEODETIC',event: 'JASON-1_OSDR_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_PLTM_GEODETIC',event: 'JASON-1_PLTM_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_SGDR_C_NETCDF_CNES_GEODETIC',event: 'JASON-1_SGDR_C_NETCDF_CNES_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_SGDR_C_NETCDF_NASA_GEODETIC',event: 'JASON-1_SGDR_C_NETCDF_NASA_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_SGDR_NETCDF_C_GEODETIC',event: 'JASON-1_SGDR_NETCDF_C_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_SIGDR_NETCDF_GEODETIC',event: 'JASON-1_SIGDR_NETCDF_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_TRSR1280_GEODETIC',event: 'JASON-1_TRSR1280_GEODETIC', federation: 'podaacDev'],
        [name: 'JASON-1_TRSR_GEODETIC',event: 'JASON-1_TRSR_GEODETIC', federation: 'podaacDev'],
        [name: 'OSTM_L2_OST_OGDR_GPS',event: 'OSTM_L2_OST_OGDR_GPS', federation: 'podaacDev'],
        [name: 'JASON-1_L2_OST_GDR_Ver-C_Binary',event: 'JASON-1_L2_OST_GDR_Ver-C_Binary', federation: 'podaacDev'],
        [name: 'MODIS_LOD',event: 'MODIS_LOD', federation: 'podaacDev'],
        [name: 'AQUARIUS_L1_CALVAL_BRIGHTNESS_TEMP',event: 'AQUARIUS_L1_CALVAL_BRIGHTNESS_TEMP', federation: 'podaacDev'],
        [name: 'AQUARIUS_L1_CALVAL_BRIGHTNESS_TEMP_V5',event: 'AQUARIUS_L1_CALVAL_BRIGHTNESS_TEMP_V5', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2C_CALVAL_RETRIEVALS',event: 'AQUARIUS_L2C_CALVAL_RETRIEVALS', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2_CALVAL_WWAVE',event: 'AQUARIUS_L2_CALVAL_WWAVE', federation: 'podaacDev'],
        [name: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V2',event: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V2', federation: 'podaacDev'],
        [name: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V3B_ASC',event: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V3B_ASC', federation: 'podaacDev'],
        [name: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V3B_NETCDF',event: 'REYNOLDS_NCEP_L4_SST_HIST_RECON_MONTHLY_V3B_NETCDF', federation: 'podaacDev'],
        [name: 'REYNOLDS_NCEP_L4_SST_OPT_INTERP_MONTHLY_V2',event: 'REYNOLDS_NCEP_L4_SST_OPT_INTERP_MONTHLY_V2', federation: 'podaacDev'],
        [name: 'REYNOLDS_NCEP_L4_SST_OPT_INTERP_WEEKLY_V2',event: 'REYNOLDS_NCEP_L4_SST_OPT_INTERP_WEEKLY_V2', federation: 'podaacDev'],
        [name: 'GOES_L3_SST_6km_NRT_SST_1HOUR',event: 'GOES_L3_SST_6km_NRT_SST_1HOUR', federation: 'podaacDev'],
        [name: 'GOES_L3_SST_6km_NRT_SST_3HOUR',event: 'GOES_L3_SST_6km_NRT_SST_3HOUR', federation: 'podaacDev'],
        [name: 'GOES_L3_SST_6km_NRT_SST_24HOUR',event: 'GOES_L3_SST_6km_NRT_SST_24HOUR', federation: 'podaacDev'],
        [name: 'OSTM_L2_OST_SDR_AMR_GDR',event: 'OSTM_L2_OST_SDR_AMR_GDR', federation: 'podaacDev'],
        [name: 'OSTM_L2_OST_SDR_AMR_IGDR',event: 'OSTM_L2_OST_SDR_AMR_IGDR', federation: 'podaacDev'],
        [name: 'AQUARIUS_L3_SSS_CAP_7DAY_V13',event: 'AQUARIUS_L3_SSS_CAP_7DAY_V13', federation: 'podaacDev'],
        [name: 'AQUARIUS_L2_SSS_CAP_V13',event: 'AQUARIUS_L2_SSS_CAP_V13', federation: 'podaacDev'],
        [name: 'OSCAR_L4_OC_third-deg_lon180',event: 'OSCAR_L4_OC_third-deg_lon180', federation: 'podaacDev'],
        [name: 'RECON_SEA_LEVEL_OST_L4_V1',event: 'RECON_SEA_LEVEL_OST_L4_V1', federation: 'podaacDev'],
        [name: 'MODIS_AQUA_L2_SST_OBPG_REFINED',event: 'MODIS_AQUA_L2_SST_OBPG_REFINED', federation: 'podaacDev'],
        [name: 'MODIS_AQUA_L2_SST_OBPG_QUICKLOOK',event: 'MODIS_AQUA_L2_SST_OBPG_QUICKLOOK', federation: 'podaacDev'],
        [name: 'MODIS_TERRA_L2_SST_OBPG_QUICKLOOK',event: 'MODIS_TERRA_L2_SST_OBPG_QUICKLOOK', federation: 'podaacDev'],
        [name: 'MODIS_TERRA_L2_SST_OBPG_REFINED',event: 'MODIS_TERRA_L2_SST_OBPG_REFINED', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_5DAY_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_5DAY_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_5DAY_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_5DAY_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_5DAY_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_5DAY_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_5DAY_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_5DAY_NIGHTTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_7DAY_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_7DAY_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_7DAY_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_7DAY_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_7DAY_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_7DAY_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_7DAY_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_7DAY_NIGHTTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_8DAY_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_8DAY_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_8DAY_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_8DAY_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_8DAY_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_8DAY_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_8DAY_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_8DAY_NIGHTTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_ANNUAL_NIGHTTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_DAILY_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_DAILY_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_DAILY_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_DAILY_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_DAILY_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_DAILY_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_DAILY_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_DAILY_NIGHTTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_DAYTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_DAYTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_DAYTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_DAYTIME_V51', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_NIGHTTIME_V5',event: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_NIGHTTIME_V5', federation: 'podaacDev'],
        [name: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_NIGHTTIME_V51',event: 'AVHRR_PATHFINDER_L3_SST_MONTHLY_NIGHTTIME_V51', federation: 'podaacDev']
  ]


  // parameters for setting up local environment
  def localParams = [
          federation: 'podaacDev',
          users: [
                  [username: 'thuang', password: Encrypt.encrypt("password"), name: 'Thomas Huang', email: 'thomas.huang@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'axt', password: Encrypt.encrypt("password"), name: "Atsuya Takagi", email: 'atsuya.takagi@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'qchau', password: Encrypt.encrypt("password"), name: "Qui Chau", email: 'qui.chau@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true]
          ],
          locations: [
                  [protocol: 'SFTP', localPath: '/Users/thuang/Development/work/storage/ingest/horizon_dev1/', remotePath: '/Users/thuang/Development/work/storage/ingest/horizon_dev1/', spaceReserved: (40L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'localhost'],
                  [protocol: 'SFTP', localPath: '/Users/thuang/Development/work/storage/ingest/horizon_dev2/', remotePath: '/Users/thuang/Development/work/storage/ingest/horizon_dev2/', spaceReserved: (40L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'localhost'],
                  [protocol: 'SFTP', localPath: '/Users/thuang/Development/work/storage/archive/', remotePath: '/Users/thuang/Development/work/storage/archive/', spaceReserved: (40L * 1024 * 1024 * 1024), stereotype: 'ARCHIVE', hostname: 'localhost'],
          ],
          storages: [
                  [name: 'localIngestDev_1', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev1/', priority: null],
                  [name: 'localIngestDev_2', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev2/', priority: null],
                  [name: 'localArchiveDev_1', localPath: '/data/dev/users/podaacdev/data/archive/', priority: null]
          ],
          remoteSystems: [
                  [rootUri: "ftp://lapinta.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://lapinta.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://localhost", organization: 'JPL', username: 'ingest', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://localhost", organization: 'JPL', username: 'ingest', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seastorm.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seastorm.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://satepsdist1e.nesdis.noaa.gov", organization: 'NOAA', username: 'anonymous', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()]
          ],
          purgeRate: 1
  ]

  // parameters for setting up development environment
  def developmentParams = [
          federation: 'podaacDev',
          users: [
                  [username: 'thuang', password: Encrypt.encrypt("password"), name: 'Thomas Huang', email: 'thomas.huang@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'axt', password: Encrypt.encrypt("password"), name: "Atsuya Takagi", email: 'atsuya.takagi@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'qchau', password: Encrypt.encrypt("password"), name: "Qui Chau", email: 'qui.chau@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'calarcon', password: Encrypt.encrypt("password"), name: "Christian Alarcon", email: 'calarcon@sdsio.jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: false, readAll: true, writeAll: true]
          ],
          locations: [
                  [protocol: 'SFTP', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev1/', remotePath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev1/', spaceReserved: (200L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'seastore.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev2/', remotePath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev2/', spaceReserved: (200L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'seastore.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/data/dev/users/podaacdev/data/archive/', remotePath: '/data/dev/users/podaacdev/data/archive/', spaceReserved: (200L * 1024 * 1024 * 1024), stereotype: 'ARCHIVE', hostname: 'seasprite.jpl.nasa.gov'],
          ],
          storages: [
                  [name: 'horizonIngestDev_1', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev1/', priority: 'HIGH'],
                  [name: 'horizonIngestDev_2', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev2/', priority: 'NORMAL'],
                  [name: 'horizonIngestDev_3', localPath: '/data/dev/users/podaacdev/data/ingest/podaacdev/horizon_dev2/', priority: 'LOW'],
                  [name: 'horizonArchiveDev_2', localPath: '/data/dev/users/podaacdev/data/archive/', priority: null]
          ],
          remoteSystems: [
                  [rootUri: "ftp://seastore.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seastore.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seasprite.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seasprite.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://lapinta.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://lapinta.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://localhost", organization: 'JPL', username: 'ingest', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seastorm.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seastorm.jpl.nasa.gov", organization: 'JPL', username: 'podaacdev', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://satepsdist1e.nesdis.noaa.gov", organization: 'NOAA', username: 'anonymous', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://satepsdist1e.nesdis.noaa.gov", organization: 'NOAA', username: 'anonymous', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seatide.jpl.nasa.gov", organization: 'JPL', username: 'ingestest', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seatide.jpl.nasa.gov", organization: 'JPL', username: 'ingestest', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()]
          ],
          purgeRate: 1
  ]

  // parameters for setting up test environment
  def testParams = [
          federation: 'podaacTest',
          users: [
                  [username: 'thuang', password: Encrypt.encrypt("password"), name: 'Thomas Huang', email: 'thomas.huang@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'axt', password: Encrypt.encrypt("password"), name: "Atsuya Takagi", email: 'atsuya.takagi@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'qchau', password: Encrypt.encrypt("password"), name: "Qui Chau", email: 'qui.chau@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'lrodrig', password: Encrypt.encrypt("password"), name: "Lela Rodriguez", email: 'Lela.Rodriguez@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'calarcon', password: Encrypt.encrypt("password"), name: "Christian Alarcon", email: 'Christian.Alarcon@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'mcknight', password: Encrypt.encrypt("password"), name: "Tim McKnight", email: 'Tim.McKnight@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'finch', password: Encrypt.encrypt("password"), name: "Chris Finch", email: 'Christopher.J.Finch@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true]

          ],
          locations: [
                  [protocol: 'SFTP', localPath: '/data/spool/staging/horizonSeadeck/', remotePath: '/data/spool/staging/horizonSeadeck/', spaceReserved: (100L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'seadeck.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/data/spool/staging/horizonSeafern/', remotePath: '/data/spool/staging/horizonSeafern/', spaceReserved: (100L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'seafern.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/store/', remotePath: '/store/', spaceReserved: (3700L * 1024 * 1024 * 1024), stereotype: 'ARCHIVE', hostname: 'seaside.jpl.nasa.gov']
          ],
          storages: [
                  [name: 'horizonSeadeck', localPath: '/data/spool/staging/horizonSeadeck/', priority: 'HIGH'],
                  [name: 'horizonSeafern', localPath: '/data/spool/staging/horizonSeafern/', priority: 'NORMAL'],
                  [name: 'horizonSeaside', localPath: '/store/', priority: 'HIGH'],
                  [name: 'horizonBubbles', localPath: '/store/', priority: 'NORMAL']
          ],
          remoteSystems: [
                  [rootUri: "ftp://satepsdist1e.nesdis.noaa.gov", organization: 'NOAA', username: 'anonymous', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seatide.jpl.nasa.gov", organization: 'JPL', username: 'ingestest', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seatide.jpl.nasa.gov", organization: 'JPL', username: 'ingestest', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seadeck.jpl.nasa.gov", organization: 'JPL', username: 'TEST', password: '', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seafern.jpl.nasa.gov", organization: 'JPL', username: 'TEST', password: '', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()]
          ],
          purgeRate: 60
  ]
  
  def testingParams = testParams

  // parameters for setting up ops environment
  def productionParams = [
          federation: 'podaacOps',
          users: [
                  [username: 'thuang', password: Encrypt.encrypt("password"), name: 'Thomas Huang', email: 'thomas.huang@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'axt', password: Encrypt.encrypt("password"), name: "Atsuya Takagi", email: 'atsuya.takagi@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'qchau', password: Encrypt.encrypt("password"), name: "Qui Chau", email: 'qui.chau@jpl.nasa.gov', role: 'devAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'lrodrig', password: Encrypt.encrypt("password"), name: "Lela Rodriguez", email: 'Lela.Rodriguez@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'calarcon', password: Encrypt.encrypt("password"), name: "Christian Alarcon", email: 'Christian.Alarcon@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'mcknight', password: Encrypt.encrypt("password"), name: "Tim McKnight", email: 'Tim.McKnight@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true],
                  [username: 'finch', password: Encrypt.encrypt("password"), name: "Chris Finch", email: 'Christopher.J.Finch@jpl.nasa.gov', role: 'deAddAndGet', capabilities: 7, admin: true, readAll: true, writeAll: true]

          ],
          locations: [
                  [protocol: 'SFTP', localPath: '/data/spool/staging/vishnu/', remotePath: '/data/spool/staging/vishnu/', spaceReserved: (100L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'vishnu.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/data/spool/staging/dmas1/', remotePath: '/data/spool/staging/dmas1/', spaceReserved: (745L * 1024 * 1024 * 1024), stereotype: 'INGEST', hostname: 'dmas1.jpl.nasa.gov'],
                  [protocol: 'SFTP', localPath: '/store/', remotePath: '/store/', spaceReserved: (6000L * 1024 * 1024 * 1024), stereotype: 'ARCHIVE', hostname: 'manta.jpl.nasa.gov']
          ],
          storages: [
                  [name: 'horizonIngestOps_vishnu', localPath: '/data/spool/staging/vishnu/', priority: 'HIGH'],
                  [name: 'horizonIngestOps_dmas1', localPath: '/data/spool/staging/dmas1/', priority: 'NORMAL'],
                  [name: 'horizonArchiveOps_manta', localPath: '/store/', priority: 'HIGH'],
                  [name: 'horizonArchiveOps_dmas2a', localPath: '/store/', priority: 'NORMAL'],
                  [name: 'horizonArchiveOps_total', localPath: '/store/', priority: null]
          ],
          remoteSystems: [
                  [rootUri: "ftp://satepsdist1e.nesdis.noaa.gov", organization: 'NOAA', username: 'anonymous', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://diamond.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seafire.jpl.nasa.gov", organization: 'JPL/OCO', username: 'ocodc', password: 'password', maxConnections: 2, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seafire.jpl.nasa.gov", organization: 'JPL', username: 'gftp', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seafire.jpl.nasa.gov", organization: 'JPL', username: 'gftp', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://vishnu.jpl.nasa.gov", organization: 'JPL', username: 'daacops', password: '', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://dmas1.jpl.nasa.gov", organization: 'JPL', username: 'daacops', password: '', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "ftp://seaurchin.jpl.nasa.gov", organization: 'JPL', username: 'gftp', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()],
                  [rootUri: "sftp://seaurchin.jpl.nasa.gov", organization: 'JPL', username: 'gftp', password: 'password', maxConnections: 10, created: new Date().getTime(), updated: new Date().getTime()]
          ],
          purgeRate: 4320
  ]
  
  def operationParams = productionParams

  /**
   * Init closure to determine what to setup
   */
  def init = {servletContext ->
    log.debug '*** BootStreap, GrailsUtil.environment: ${GrailsUtil.environment}'

    switch (GrailsUtil.environment) {
      case 'local':
      case 'oracle':
        log.debug '*** detected local'
        this.config(localParams, 'thuang')
        break
      case 'development':
        log.debug '*** detected development'
        this.config(developmentParams, 'thuang')
        break;
      case 'test':
        log.debug '*** detected test'
        //this.config(testParams, 'lrodrig')
        this.config(testParams, 'thuang')
        break;
      case 'testing':
        log.debug '*** detected testing'
        //this.config(testParams, 'lrodrig')
        this.config(testingParams, 'thuang')
        break;
      case 'production':
         log.debug '*** detected production'
         //this.config(opsParams, 'mckinight')
         this.config(productionParams, 'thuang')
         break;
      case 'operation':
        log.debug '*** detected operation'
        //this.config(testParams, 'lrodrig')
        this.config(operationParams, 'thuang')
        break;
    }
    
    log.debug("Cleaning up EngineJobs.")
    cleanEngineJobs()
    log.debug("Done cleaning up EngineJobs.")
    
    quartzScheduler.start()
  }

  def destroy = {
  }

  /**
   * Method to bootstrap environment
   */
  void config(def params, String admin) {

    def federation = params.federation
    def engines = params.engines
    def users = params.users
    def locations = params.locations
    def storages = params.storages
    def remoteSystems = params.remoteSystems

    log.debug("Setup federation '${federation}'.")
    // register federation
    def fed = IngFederation.findByName(federation)
    if (!fed) {
      fed = new IngFederation(
              name: federation,
              note: "PO.DAAC ${federation} Federation",
              updated: new Date().getTime(),
              hostname: System.getProperty('server.host') ?: 'localhost',
              port: (System.getProperty('server.port.https') ?: 8443).toInteger()
      );
    } else {
       fed.updated = new Date().getTime()
       fed.hostname = System.getProperty('server.host') ?: 'localhost'
       fed.port = (System.getProperty('server.port.https') ?: 8443).toInteger()
    }
    if (!fed.save(flush: true)) {
       fed.errors.each {
         log.error it
       }
    }

    /*
    // register access role
    IngAccessRole devRole = IngAccessRole.findByName("devAddAndGet")
    if (!devRole) {
      devRole = new IngAccessRole(
              name: 'devAddAndGet',
              capabilities: 7
      )
      if (!devRole.save(flush: true)) {
        devRole.errors.each {
          log.error it
        }
      }
    }
    */

    // register users
    users.each {rec ->
      IngSystemUser user = IngSystemUser.findByName(rec.username)
      if (!user) {
        IngAccessRole accessRole = IngAccessRole.findByName(rec.role)
        if (!accessRole) {
          accessRole = new IngAccessRole(
                  name: rec.role,
                  capabilities: rec.capabilities
          )
          if (!accessRole.save(flush: true)) {
            accessRole.errors.each {
              log.error it
            }
          }
        }
        
        user = new IngSystemUser(
                name: rec.username,
                password: rec.password,
                fullname: rec.name,
                email: rec.email,
                admin: rec.admin,
                readAll: rec.readAll,
                writeAll: rec.writeAll,
                note: rec.name)
        if (!user.save(flush: true)) {
          user.errors.each {
            log.error it
          }
        }

        IngSystemUserRole userRole = new IngSystemUserRole(
                user: user,
                role: accessRole
        )
        if (!userRole.save(flush: true)) {
          userRole.errors.each {
            log.error it
          }
        }
      }
    }

    // register product types
    productTypes.each {
      if (it.federation.equals(fed.name)) {
      IngEventCategory category = IngEventCategory.findByName(it.event)
      if (!category) {
         category = new IngEventCategory(
               name: it.event
         )
         category.save(flush:true)
      }

      def productTypeName = (it.prefix) ? it.name.substring(it.prefix.length()) : it.name
      IngProductType productType = IngProductType.findByName(productTypeName)
      if (!productType) {
        // it could be that we removed prefix, so check for that
        productType = IngProductType.findByFederationAndName(fed, it.name)
        if(!productType) {
           productType = new IngProductType(
                   federation: fed,
                   name: productTypeName,
                   locked: false,
                   ingestOnly: false,
                   relativePath: "${productTypeName}/",
                   purgeRate: params.purgeRate,
                   updatedBy: IngSystemUser.findByName(admin),
                   updated: new Date().getTime(),
                   note: "${productTypeName} product",
                   eventCategory: category
           )
           if (!productType.save(flush: true)) {
             productType.errors.each {
               log.error it
             }
           }
   
           def accessRoles = IngAccessRole.getAll()
           accessRoles.each {accessRole ->
             IngProductTypeRole ptRole = new IngProductTypeRole(
                     productType: productType,
                     role: accessRole
             )
             if (!ptRole.save(flush: true)) {
               ptRole.errors.each {
                 log.error it
               }
             }
           }
        }
      } else {
         if (!productType.eventCategory || !productType.eventCategory.equals(category)) {
            log.trace("Update event category for ${productType.name}")
            productType.eventCategory = category
         }
         if (productType.federation.id != fed.id) {
            log.trace("Update federation for ${productType.name}")
            productType.federation = fed
         }
         if (it.priority) {
            def priority = JobPriority.valueOf(it.priority).value
            if (productType.priority != priority) {
               productType.priority = priority
               log.trace("Changing priority for ${productType.name} to ${it.priority}")
            }
         }
         if (productType.isDirty()) {
            log.trace("Save ${productType.name}")
            productType.save(flush: true)
         }
      }
      }
    }

    // register engine storages
    locations.each {rec ->
      IngLocation location = IngLocation.findByLocalPath(rec.localPath)
      if (!location) {
        location = new IngLocation(
                localPath: rec.localPath,
                remotePath: rec.remotePath,
                remoteAccessProtocol: rec.protocol,
                spaceReserved: rec.spaceReserved,
                spaceThreshold: rec.spaceReserved * 0.8,
                stereotype: rec.stereotype,
                hostname: rec.hostname
        )
        if (!location.save(flush: true)) {
          location.errors.each {
            log.error it
          }
        }
      }
    }
    
    storages.each {rec ->
      IngLocation location = IngLocation.findByLocalPath(rec.localPath)
      if (!location) {
         log.error("Location with local path ${rec.localPath} does not exists. Cannot map storage ${rec.name} to this location.")
      } else {
         IngStorage storage = IngStorage.findByName(rec.name)
         def storagePriority = rec.priority ? JobPriority.valueOf(rec.priority).value : null
         if (!storage) {
           storage = new IngStorage(
                   name: rec.name,
                   priority: storagePriority,
                   location: location
           )
           if (!storage.save(flush: true)) {
             storage.errors.each {
               log.error it
             }
           }
         } else {
            if (storage.priority != storagePriority) {
               storage.priority = storagePriority
               log.debug("Changing priority for storage ${storage.name} to ${rec.priority}")
            }
            if (storage.isDirty()) {
               log.trace("Save storage ${storage.name}")
               storage.save(flush: true)
            }
         }
         //Create node /engines/[storageName] so engine can register with this storage
         registerStorage(fed.name, rec.name)
      }
    }

    // register remote systems
    remoteSystems.each {system ->
      IngRemoteSystem remoteSystem = IngRemoteSystem.findByRootUri(system.rootUri)
      if (!remoteSystem) {
        remoteSystem = new IngRemoteSystem(
                rootUri: system.rootUri,
                organization: system.organization,
                username: system.username,
                password: system.password,
                maxConnections: system.maxConnections,
                created: system.created,
                updated: system.updated,
                updatedBy: admin/*IngSystemUser.findByName(admin)*/
        )
        if (!remoteSystem.save(flush: true)) {
          remoteSystem.errors.each {
            log.error it
          }
        }
      }
    }
    
    // store federation for later use
    ConfigurationHolder.config.put("manager_federation", federation)
  }
  
  private void cleanEngineJobs() {
    def query = {
       product {
          productType {
             federation {
                eq("name", ConfigurationHolder.config.manager_federation)
             }
          }
       }
    }
    def engineJobsCount = IngEngineJob.createCriteria().count(query)
    def engineJobsIndex = 0
    while(engineJobsIndex < engineJobsCount) {
      def engineJobs = IngEngineJob.createCriteria().list(
        max: BootStrap.ENGINE_JOBS_PER_PAGING,
        offset: engineJobsIndex,
        order: "desc",
        sort: "assigned",
        query
      )
      def watcher = { WatchedEvent event ->
         log.debug ("BootStrap:cleanEngineJobs Watcher: received event " + event.type + " " + event.state)
      }
      engineJobs.each {engineJob ->
         ZkAccess zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
         
         if (engineJob.path) {
            if (!zk.removeNode(engineJob.path)) {
               zk.removeProcessNode(engineJob.product.productType.name+"/"+engineJob.product.name)
            }
         }
         
         // put storage back as needed
         if ([Lock.ADD, Lock.REPLACE, Lock.ARCHIVE].find{it == Lock.valueOf(engineJob.operation)} 
               && [State.PENDING, State.PENDING_STORAGE, State.PENDING_ARCHIVE, State.PENDING_ARCHIVE_STORAGE].find{it == State.valueOf(engineJob.previousState)}) {
            long totalSize = engineJob.product.files.sum {
               it.fileSize
            }
            storageService.updateStorage(-totalSize, new Date().getTime(), engineJob.contributeStorage.location.id)
         }

        try {
          IngProduct product = IngProduct.get(engineJob.product.id)
          product.currentLock = engineJob.operation
          product.currentState = engineJob.previousState
          if(!product.save(flush: true)) {
             throw new Exception("Failed to save product: "+product.errors.allErrors.join())
          }
          
          engineJob.delete()
          log.debug("Cleaned EngineJob["+engineJob.id+"].")
        } catch(Exception exception) {
           log.error("Failed to rollback enginejob.", exception)
        }
      }
      
      engineJobsIndex += BootStrap.ENGINE_JOBS_PER_PAGING
    }
  }
  
  private void registerStorage(String federationName, String storageName) {
     def watcher = { WatchedEvent event ->
        log.debug ("BootStrap:registerStorage Watcher: received event " + event.type + " " + event.state)
     }
     ZkAccess zk = ZkFactory.getZk(ConfigurationHolder.config.horizon_zookeeper_url as String, watcher as Watcher)
     if (!zk.nodeExists("${ZkAccess.ENGINE_REGISTRATION_ROOT}/$storageName")) {
        String path = zk.createNode("${ZkAccess.ENGINE_REGISTRATION_ROOT}/$storageName", "$federationName manager registered storage");
        log.debug("Registered storage with zookeeper with path $path")
     }
  }
}
