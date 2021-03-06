<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-05-03 12:32:00'
LAST_UPDATE='2007-09-08 09:40:53'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ETS={'xs:%complexType';'xs:group'}
<TEMPLATE_PARAMS>
	PARAM={
		param.name='owner_id';
		param.description='The element identifier of the schema component containing this model';
		param.type='object';
	}
</TEMPLATE_PARAMS>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs2';
}
<STYLES>
	CHAR_STYLE={
		style.name='Default Paragraph Font';
		style.id='cs1';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Hyperlink';
		style.id='cs2';
		text.decor.underline='true';
		text.color.foreground='#0000FF';
	}
	PAR_STYLE={
		style.name='Normal';
		style.id='s1';
		style.default='true';
	}
</STYLES>
<ROOT>
	<FOLDER>
		FMT={
			sec.outputStyle='text-par';
		}
		<BODY>
			<FOLDER>
				MATCHING_ET='xs:%complexType'
				<BODY>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								FMT={
									txtfl.delimiter.type='text';
									txtfl.delimiter.text=' \u00d7 ';
								}
								<CTRLS>
									<LABEL>
										COND='((el = findChild("xs:complexContent")) != null && el.hasAttr ("mixed")) ?\n  el.getAttrBooleanValue("mixed") : getAttrBooleanValue("mixed");'
										TEXT='{text}'
									</LABEL>
									<SS_CALL_CTRL>
										SS_NAME='complexType'
									</SS_CALL_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</BODY>
			</FOLDER>
			<SS_CALL>
				MATCHING_ET='xs:group'
				SS_NAME='group'
			</SS_CALL>
		</BODY>
	</FOLDER>
</ROOT>
<STOCK_SECTIONS>
	<ELEMENT_ITER>
		MATCHING_ET='xs:%all'
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='text';
			txtfl.delimiter.text=' \u00d7 ';
		}
		TARGET_ET='xs:element%xs:narrowMaxMin'
		SCOPE='simple-location-rules'
		RULES={
			'* -> xs:element%xs:narrowMaxMin';
		}
		SS_NAME='all'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='none';
						}
						<CTRLS>
							<DATA_CTRL>
								DESCR='case of global element'
								COND='getAttrStringValue("ref") != ""'
								<DOC_HLINK>
									HKEYS={
										'findElementByKey ("global-elements", getAttrQNameValue("ref")).id';
										'"detail"';
									}
								</DOC_HLINK>
								ATTR='ref'
							</DATA_CTRL>
							<SS_CALL_CTRL>
								DESCR='case of local element'
								COND='getAttrStringValue("ref") == ""'
								SS_NAME='localElement'
							</SS_CALL_CTRL>
							<LABEL>
								COND='getAttrIntValue("minOccurs") == 0 &&\ngetAttrStringValue("maxOccurs") == "1"'
								TEXT='?'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
		<HEADER>
			<AREA_SEC>
				COND='getAttrIntValue("minOccurs") == 0'
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='('
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
		<FOOTER>
			<AREA_SEC>
				COND='getAttrIntValue("minOccurs") == 0'
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT=')?'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</FOOTER>
	</ELEMENT_ITER>
	<FOLDER>
		MATCHING_ET='xs:any'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='any'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='none';
						}
						<CTRLS>
							<LABEL>
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"local"';
										'getParam("owner_id")';
									}
								</DOC_HLINK>
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"def"';
									}
								</DOC_HLINK>
								TEXT='{any}'
							</LABEL>
							<SS_CALL_CTRL>
								SS_NAME='Occurrence Operator'
							</SS_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
	<FOLDER>
		MATCHING_ETS={'xs:choice';'xs:choice%xs:simpleExplicitGroup'}
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='text';
			txtfl.delimiter.text=' | ';
		}
		SS_NAME='choice'
		<BODY>
			<ELEMENT_ITER>
				COND='stockSection.param == "sequence_context"\n&& countChildren("xs:element | xs:%group") > 1\n|| checkStockSectionOutput ("Occurrence Operator")'
				TARGET_ETS={'xs:%element';'xs:%group';'xs:any'}
				SCOPE='simple-location-rules'
				RULES={
					'* -> (xs:%element|xs:%group|xs:any)';
				}
				<BODY>
					<SS_CALL>
						MATCHING_ET='xs:any'
						SS_NAME='any'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%element'
						SS_NAME='element'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%groupRef'
						SS_NAME='groupRef'
						PARAMS_EXPR='iterator.numItems > 1 ? Array ("choice_context") : null'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:choice'
						SS_NAME='choice'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:sequence'
						SS_NAME='sequence'
						PARAMS_EXPR='iterator.numItems > 1 ? Array ("choice_context") : null'
					</SS_CALL>
				</BODY>
				<HEADER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										COND='iterator.numItems > 1'
										TEXT='('
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</HEADER>
				<FOOTER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								FMT={
									txtfl.delimiter.type='none';
								}
								<CTRLS>
									<LABEL>
										COND='iterator.numItems > 1'
										TEXT=')'
									</LABEL>
									<SS_CALL_CTRL>
										SS_NAME='Occurrence Operator'
									</SS_CALL_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</FOOTER>
			</ELEMENT_ITER>
			<ELEMENT_ITER>
				COND='sectionBlock.execSecNone'
				TARGET_ETS={'xs:%element';'xs:%group';'xs:any'}
				SCOPE='simple-location-rules'
				RULES={
					'* -> (xs:%element|xs:%group|xs:any)';
				}
				<BODY>
					<SS_CALL>
						MATCHING_ET='xs:any'
						SS_NAME='any'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%element'
						SS_NAME='element'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%groupRef'
						SS_NAME='groupRef'
						PARAMS_EXPR='Array ("choice_context")'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:choice'
						SS_NAME='choice'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:sequence'
						SS_NAME='sequence'
						PARAMS_EXPR='Array ("choice_context")'
					</SS_CALL>
				</BODY>
			</ELEMENT_ITER>
		</BODY>
	</FOLDER>
	<FOLDER>
		MATCHING_ET='xs:complexContent'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='complexContent'
		<BODY>
			<ELEMENT_ITER>
				DESCR='DERIVATION BY EXTENSION'
				TARGET_ET='xs:extension%xs:extensionType'
				SCOPE='simple-location-rules'
				RULES={
					'* -> xs:extension%xs:extensionType';
				}
				<BODY>
					<FOLDER>
						DESCR='if content model is defined both by the base type and by the locally defined particle,\nthen treat the whole thing as a sequence'
						COND='baseType = findElementByKey ("types", getAttrQNameValue("base"));\n\nbaseType->checkStockSectionOutput ("complexType") && \ncheckStockSectionOutput("contentParticle")'
						FMT={
							txtfl.delimiter.type='text';
							txtfl.delimiter.text=', ';
						}
						<BODY>
							<SS_CALL>
								SS_NAME='complexType'
								PASSED_ELEMENT_EXPR='findElementByKey ("types", getAttrQNameValue("base"));'
								PASSED_ELEMENT_MATCHING_ET='xs:%complexType'
								PARAMS_EXPR='Array ("sequence_context")'
							</SS_CALL>
							<SS_CALL>
								SS_NAME='contentParticle'
								PARAMS_EXPR='Array ("sequence_context")'
							</SS_CALL>
						</BODY>
					</FOLDER>
					<FOLDER>
						DESCR='otherwise'
						COND='sectionBlock.execSecNone'
						<BODY>
							<SS_CALL>
								SS_NAME='complexType'
								PASSED_ELEMENT_EXPR='findElementByKey ("types", getAttrQNameValue("base"))'
								PASSED_ELEMENT_MATCHING_ET='xs:%complexType'
								PARAMS_EXPR='stockSection.params'
							</SS_CALL>
							<SS_CALL>
								SS_NAME='contentParticle'
								PARAMS_EXPR='stockSection.params'
							</SS_CALL>
						</BODY>
					</FOLDER>
				</BODY>
			</ELEMENT_ITER>
			<ELEMENT_ITER>
				DESCR='DERIVATION BY RESTRICTION'
				TARGET_ET='xs:restriction%xs:complexRestrictionType'
				SCOPE='simple-location-rules'
				RULES={
					'* -> xs:restriction%xs:complexRestrictionType';
				}
				<BODY>
					<SS_CALL>
						SS_NAME='contentParticle'
						PARAMS_EXPR='stockSection.params'
					</SS_CALL>
				</BODY>
			</ELEMENT_ITER>
		</BODY>
	</FOLDER>
	<FOLDER>
		MATCHING_ET='xs:%complexType'
		OUTPUT_CHECKER_EXPR='checkElementsByKey ("content-elements", contextElement.id) ? 1 : -1'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='complexType'
		<BODY>
			<SS_CALL>
				SS_NAME='complexContent'
				PASSED_ELEMENT_EXPR='findChild("xs:complexContent")'
				PASSED_ELEMENT_MATCHING_ET='xs:complexContent'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				SS_NAME='contentParticle'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
		</BODY>
	</FOLDER>
	<ELEMENT_ITER>
		MATCHING_ETS={'xs:%complexType';'xs:extension%xs:extensionType';'xs:restriction%xs:complexRestrictionType'}
		TARGET_ET='xs:%group'
		SCOPE='simple-location-rules'
		RULES={
			'* -> xs:%group';
		}
		SS_NAME='contentParticle'
		<BODY>
			<SS_CALL>
				MATCHING_ET='xs:%groupRef'
				SS_NAME='groupRef'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				MATCHING_ET='xs:%all'
				SS_NAME='all'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				MATCHING_ET='xs:choice'
				SS_NAME='choice'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				MATCHING_ET='xs:sequence'
				SS_NAME='sequence'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
		</BODY>
	</ELEMENT_ITER>
	<FOLDER>
		MATCHING_ET='xs:%localElement'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='element'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='none';
						}
						<CTRLS>
							<DATA_CTRL>
								DESCR='case of global element'
								COND='getAttrStringValue("ref") != ""'
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"local"';
										'getParam("owner_id")';
									}
								</DOC_HLINK>
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"def"';
									}
								</DOC_HLINK>
								<DOC_HLINK>
									HKEYS={
										'findElementByKey ("global-elements", getAttrQNameValue("ref")).id';
										'"detail"';
									}
								</DOC_HLINK>
								ATTR='ref'
							</DATA_CTRL>
							<SS_CALL_CTRL>
								DESCR='case of local element'
								COND='getAttrStringValue("ref") == ""'
								SS_NAME='localElement'
							</SS_CALL_CTRL>
							<SS_CALL_CTRL>
								SS_NAME='Occurrence Operator'
							</SS_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
	<ELEMENT_ITER>
		MATCHING_ET='xs:group'
		OUTPUT_CHECKER_EXPR='checkElementsByKey ("content-elements", contextElement.id) ? 1 : -1'
		TARGET_ET='xs:%group'
		SCOPE='simple-location-rules'
		RULES={
			'* -> xs:%group';
		}
		SS_NAME='group'
		<BODY>
			<SS_CALL>
				SS_NAME='all'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				MATCHING_ET='xs:choice%xs:simpleExplicitGroup'
				SS_NAME='choice'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
			<SS_CALL>
				MATCHING_ET='xs:sequence%xs:simpleExplicitGroup'
				SS_NAME='sequence'
				PARAMS_EXPR='stockSection.params'
			</SS_CALL>
		</BODY>
	</ELEMENT_ITER>
	<FOLDER>
		MATCHING_ET='xs:%groupRef'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='groupRef'
		<BODY>
			<FOLDER>
				COND='checkStockSectionOutput ("Occurrence Operator")'
				<BODY>
					<SS_CALL>
						SS_NAME='group'
						PASSED_ELEMENT_EXPR='findElementByKey ("groups", getAttrQNameValue("ref"))'
						PASSED_ELEMENT_MATCHING_ET='xs:group'
					</SS_CALL>
				</BODY>
				<HEADER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='('
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</HEADER>
				<FOOTER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								FMT={
									txtfl.delimiter.type='none';
								}
								<CTRLS>
									<LABEL>
										TEXT=')'
									</LABEL>
									<SS_CALL_CTRL>
										SS_NAME='Occurrence Operator'
									</SS_CALL_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</FOOTER>
			</FOLDER>
			<FOLDER>
				COND='sectionBlock.execSecNone'
				<BODY>
					<SS_CALL>
						SS_NAME='group'
						PASSED_ELEMENT_EXPR='findElementByKey ("groups", getAttrQNameValue("ref"))'
						PASSED_ELEMENT_MATCHING_ET='xs:group'
						PARAMS_EXPR='stockSection.params'
					</SS_CALL>
				</BODY>
			</FOLDER>
		</BODY>
	</FOLDER>
	<AREA_SEC>
		MATCHING_ET='xs:%element'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='localElement'
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<DATA_CTRL>
						<DOC_HLINK>
							HKEYS={
								'contextElement.id';
								'"local"';
								'getParam("owner_id")';
							}
						</DOC_HLINK>
						<DOC_HLINK>
							HKEYS={
								'contextElement.id';
								'"def"';
							}
						</DOC_HLINK>
						<DOC_HLINK>
							HKEYS={
								'contextElement.id';
								'"detail"';
							}
						</DOC_HLINK>
						FORMULA='schema = getXMLDocument().findChild ("xs:schema");\n\n((form = getAttrStringValue("form")) == "") ? {\n  form = schema.getAttrStringValue ("elementFormDefault");\n};\n\nname = getAttrStringValue("name");\n\n(form != "qualified") ? name : {\n   QName (schema.getAttrStringValue("targetNamespace"), name).toString()\n};'
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<FOLDER>
		COND='(hasAttr("minOccurs") ||  hasAttr("maxOccurs")) &&\n(getAttrIntValue("minOccurs") != 1 ||  getAttrIntValue("maxOccurs") != 1)'
		MATCHING_ETS={'xs:%element';'xs:%group';'xs:any'}
		SS_NAME='Occurrence Operator'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								COND='getAttrIntValue("minOccurs") == 0 &&\ngetAttrIntValue("maxOccurs") == 1'
								TEXT='?'
							</LABEL>
							<LABEL>
								COND='getAttrIntValue("minOccurs") == 1 &&\ngetAttrStringValue("maxOccurs") == "unbounded"'
								TEXT='+'
							</LABEL>
							<LABEL>
								COND='getAttrIntValue("minOccurs") == 0 &&\ngetAttrStringValue("maxOccurs") == "unbounded"'
								TEXT='*'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='sectionBlock.outputSecNone'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='none';
						}
						<CTRLS>
							<LABEL>
								TEXT='['
							</LABEL>
							<DATA_CTRL>
								ATTR='minOccurs'
							</DATA_CTRL>
							<LABEL>
								TEXT='..'
							</LABEL>
							<DATA_CTRL>
								FORMULA='(maxOccurs = getAttrStringValue("maxOccurs")) == "unbounded" ? \n"*" : maxOccurs'
							</DATA_CTRL>
							<LABEL>
								TEXT=']'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
	<FOLDER>
		MATCHING_ETS={'xs:sequence';'xs:sequence%xs:simpleExplicitGroup'}
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='text';
			txtfl.delimiter.text=', ';
		}
		SS_NAME='sequence'
		<BODY>
			<ELEMENT_ITER>
				COND='stockSection.param == "choice_context" && countChildren("xs:element | xs:%group") > 1\n|| checkStockSectionOutput ("Occurrence Operator")'
				TARGET_ETS={'xs:%element';'xs:%group';'xs:any'}
				SCOPE='simple-location-rules'
				RULES={
					'* -> (xs:%element|xs:%group|xs:any)';
				}
				<BODY>
					<SS_CALL>
						MATCHING_ET='xs:any'
						SS_NAME='any'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%element'
						SS_NAME='element'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%groupRef'
						SS_NAME='groupRef'
						PARAMS_EXPR='iterator.numItems > 1 ? Array ("sequence_context") : null'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:choice'
						SS_NAME='choice'
						PARAMS_EXPR='iterator.numItems > 1 ? Array ("sequence_context") : null'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:sequence'
						SS_NAME='sequence'
						PARAMS_EXPR='Array ("sequence_context")'
					</SS_CALL>
				</BODY>
				<HEADER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										COND='iterator.numItems > 1'
										TEXT='('
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</HEADER>
				<FOOTER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								FMT={
									txtfl.delimiter.type='none';
								}
								<CTRLS>
									<LABEL>
										COND='iterator.numItems > 1'
										TEXT=')'
									</LABEL>
									<SS_CALL_CTRL>
										SS_NAME='Occurrence Operator'
									</SS_CALL_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</FOOTER>
			</ELEMENT_ITER>
			<ELEMENT_ITER>
				COND='sectionBlock.execSecNone'
				TARGET_ETS={'xs:%element';'xs:%group';'xs:any'}
				SCOPE='simple-location-rules'
				RULES={
					'* -> (xs:%element|xs:%group|xs:any)';
				}
				<BODY>
					<SS_CALL>
						MATCHING_ET='xs:any'
						SS_NAME='any'
						PARAMS_EXPR='Array ("sequence_context")'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%element'
						SS_NAME='element'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:%groupRef'
						SS_NAME='groupRef'
						PARAMS_EXPR='Array ("sequence_context")'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:choice'
						SS_NAME='choice'
						PARAMS_EXPR='Array ("sequence_context")'
					</SS_CALL>
					<SS_CALL>
						MATCHING_ET='xs:sequence'
						SS_NAME='sequence'
						PARAMS_EXPR='Array ("sequence_context")'
					</SS_CALL>
				</BODY>
			</ELEMENT_ITER>
		</BODY>
	</FOLDER>
</STOCK_SECTIONS>
CHECKSUM='fTOiH7CKQa4quzS3zdSdZg'
</DOCFLEX_TEMPLATE>
