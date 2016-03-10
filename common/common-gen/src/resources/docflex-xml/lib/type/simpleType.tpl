<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-04-26 03:31:00'
LAST_UPDATE='2007-09-08 09:40:54'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ET='xs:simpleType'
<TEMPLATE_PARAMS>
	PARAM={
		param.name='nsURI';
		param.displayName='Target Namespace URI';
		param.type='string';
		param.defaultExpr='schema = getXMLDocument().findChild ("xs:schema");\nschema.getAttrStringValue("targetNamespace")';
		param.hidden='true';
	}
	PARAM={
		param.name='qName';
		param.description='The <code>QName</code> object representing the global type\'s qualified name.\n<p>\nSee Expr. Assistant | XML Functions | <code>QName()</code> function.';
		param.type='object';
		param.defaultExpr='QName (getStringParam("nsURI"), getAttrStringValue("name"))';
	}
	PARAM={
		param.name='usageCount';
		param.description='number of locations where this type is used';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "type-usage",\n  getParam("qName")\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='attributeCount';
		param.displayName='number of all attributes';
		param.description='total number of attributes declared for this component';
		param.type='int';
		param.hidden='true';
	}
	PARAM={
		param.name='elementCount';
		param.displayName='number of all content elements';
		param.description='total number of content elements declared for this component';
		param.type='int';
		param.hidden='true';
	}
	PARAM={
		param.name='ownAttributeCount';
		param.displayName='number of component\'s own attributes';
		param.description='number of attributes defined within this component';
		param.type='int';
		param.hidden='true';
	}
	PARAM={
		param.name='ownElementCount';
		param.displayName='number of component\'s own content elements';
		param.description='number of content elements defined within this component';
		param.type='int';
		param.hidden='true';
	}
	PARAM={
		param.name='anyAttribute';
		param.displayName='component has any-attribute';
		param.description='indicates that the component allows any attributes';
		param.type='boolean';
		param.hidden='true';
	}
	PARAM={
		param.name='anyElement';
		param.displayName='component has any-content-element';
		param.description='indicates that the component allows any content elements';
		param.type='boolean';
		param.hidden='true';
	}
	PARAM={
		param.name='doc.simpleType.profile';
		param.displayName='Simple Type Profile';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.xmlRep';
		param.displayName='XML Representation Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.list.directSubtypes';
		param.displayName='List of Direct Subtypes';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.list.indirectSubtypes';
		param.displayName='List of Indirect Subtypes';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.list.basedElements';
		param.displayName='List of All Based Elements';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.list.basedAttributes';
		param.displayName='List of All Based Attributes';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.list.usage';
		param.displayName='Usage Locations';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.annotation';
		param.displayName='Annotation';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.typeDef';
		param.displayName='Type Definition Detail';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.simpleType.xmlSource';
		param.displayName='XML Source';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='sec.xmlSource.box.component';
		param.displayName='Enclose in box';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='sec.xmlSource.remove.ann.component';
		param.displayName='Remove <xs:annotation> elements';
		param.type='boolean';
		param.boolean.default='true';
	}
</TEMPLATE_PARAMS>
<HTARGET>
	HKEYS={
		'contextElement.id';
		'"detail"';
	}
</HTARGET>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs4';
}
<STYLES>
	CHAR_STYLE={
		style.name='Code Smaller';
		style.id='cs1';
		text.font.name='Courier New';
		text.font.size='8';
	}
	CHAR_STYLE={
		style.name='Code Smallest';
		style.id='cs2';
		text.font.name='Courier New';
		text.font.size='7.5';
	}
	CHAR_STYLE={
		style.name='Default Paragraph Font';
		style.id='cs3';
		style.default='true';
	}
	PAR_STYLE={
		style.name='Detail Heading 1';
		style.id='s1';
		text.font.size='12';
		text.font.style.bold='true';
		par.bkgr.opaque='true';
		par.bkgr.color='#CCCCFF';
		par.border.style='solid';
		par.border.color='#666666';
		par.margin.top='12';
		par.margin.bottom='10';
		par.padding.left='2.5';
		par.padding.right='2.5';
		par.padding.top='2';
		par.padding.bottom='2';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Detail Heading 2';
		style.id='s2';
		text.font.size='9';
		text.font.style.bold='true';
		par.bkgr.opaque='true';
		par.bkgr.color='#EEEEFF';
		par.border.style='solid';
		par.border.color='#666666';
		par.margin.top='12';
		par.margin.bottom='10';
		par.padding.left='2';
		par.padding.right='2';
		par.padding.top='2';
		par.padding.bottom='2';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Detail Heading 3';
		style.id='s3';
		text.font.size='9';
		text.font.style.bold='true';
		text.font.style.italic='true';
		text.color.background='#CCCCFF';
		text.color.opaque='true';
		par.margin.top='10';
		par.margin.bottom='8';
		par.page.keepWithNext='true';
	}
	CHAR_STYLE={
		style.name='Hyperlink';
		style.id='cs4';
		text.decor.underline='true';
		text.color.foreground='#0000FF';
	}
	PAR_STYLE={
		style.name='Inset Heading';
		style.id='s4';
		text.font.style.bold='true';
		text.color.foreground='#990000';
		par.margin.bottom='6.8';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Inset Heading 2';
		style.id='s5';
		text.font.size='9';
		text.font.style.bold='true';
		text.color.foreground='#990000';
		par.margin.bottom='6.8';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='List Heading 1';
		style.id='s6';
		text.font.name='Arial';
		text.font.size='10';
		text.font.style.bold='true';
		par.margin.top='12';
		par.margin.bottom='8';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Main Heading';
		style.id='s7';
		text.font.name='Verdana';
		text.font.size='13';
		text.font.style.bold='true';
		text.color.foreground='#4477AA';
		par.bkgr.opaque='true';
		par.bkgr.color='#EEEEEE';
		par.border.style='solid';
		par.border.color='#4477AA';
		par.margin.top='0';
		par.margin.bottom='9';
		par.padding.left='5';
		par.padding.right='5';
		par.padding.top='3';
		par.padding.bottom='3';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Normal';
		style.id='s8';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Normal Smaller';
		style.id='cs5';
		text.font.name='Arial';
		text.font.size='9';
	}
	CHAR_STYLE={
		style.name='Note Font';
		style.id='cs6';
		text.font.name='Arial';
		text.font.size='8';
		text.font.style.bold='false';
		par.lineHeight='11';
		par.margin.right='7';
	}
	CHAR_STYLE={
		style.name='Page Header Font';
		style.id='cs7';
		text.font.name='Arial';
		text.font.style.italic='true';
	}
	CHAR_STYLE={
		style.name='Page Number Small';
		style.id='cs8';
		text.font.name='Courier New';
		text.font.size='8';
	}
	PAR_STYLE={
		style.name='Properties Heading';
		style.id='s9';
		text.font.name='Arial';
		text.font.size='8';
		text.font.style.bold='true';
		text.font.style.italic='true';
		text.color.background='#CCCCFF';
		text.color.opaque='true';
		par.margin.top='6';
		par.margin.bottom='6';
		par.page.keepWithNext='true';
	}
	CHAR_STYLE={
		style.name='Property Text';
		style.id='cs9';
		text.font.name='Verdana';
		text.font.size='8';
		par.lineHeight='11';
	}
	PAR_STYLE={
		style.name='Property Title';
		style.id='s10';
		text.font.size='8';
		text.font.style.bold='true';
		par.lineHeight='11';
		par.margin.right='7';
	}
	PAR_STYLE={
		style.name='Property Value';
		style.id='s11';
		text.font.name='Verdana';
		text.font.size='8';
		par.lineHeight='11';
	}
</STYLES>
<PAGE_HEADER>
	<AREA_SEC>
		FMT={
			text.style='cs7';
			table.cellpadding.both='0';
			table.border.style='none';
			table.border.bottom.style='solid';
		}
		<AREA>
			<CTRL_GROUP>
				FMT={
					par.border.bottom.style='solid';
				}
				<CTRLS>
					<LABEL>
						TEXT='simpleType'
					</LABEL>
					<DATA_CTRL>
						FMT={
							text.font.style.italic='true';
						}
						FORMULA='\'"\' + getParam("qName") + \'"\''
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
</PAGE_HEADER>
<ROOT>
	<AREA_SEC>
		FMT={
			par.style='s7';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						TEXT='simpleType'
					</LABEL>
					<DATA_CTRL>
						FMT={
							text.font.style.italic='true';
						}
						FORMULA='\'"\' + getParam("qName") + \'"\''
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<TEMPLATE_CALL>
		DESCR='Type Profile'
		COND='getBooleanParam("doc.simpleType.profile")'
		TEMPLATE_FILE='typeProfile.tpl'
	</TEMPLATE_CALL>
	<AREA_SEC>
		COND='getBooleanParam("doc.simpleType.xmlRep")'
		FMT={
			sec.outputStyle='table';
			sec.spacing.before='12';
			sec.spacing.after='12';
			table.sizing='Relative';
			table.cellpadding.both='5';
			table.bkgr.color='#F5F5F5';
			table.border.style='solid';
			table.border.color='#999999';
			table.page.keepTogether='true';
			table.option.borderStylesInHTML='true';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<SS_CALL_CTRL>
						FMT={
							ctrl.size.width='499.5';
							ctrl.size.height='17.3';
						}
						SS_NAME='Datatype Representation'
					</SS_CALL_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<TEMPLATE_CALL>
		DESCR='Lists of other related components'
		TEMPLATE_FILE='relatedCompLists.tpl'
		PASSED_PARAMS={
			'indirectSubtypes','getBooleanParam("doc.simpleType.list.indirectSubtypes")';
			'basedAttributes','getBooleanParam("doc.simpleType.list.basedAttributes")';
			'basedElements','getBooleanParam("doc.simpleType.list.basedElements")';
			'directSubtypes','getBooleanParam("doc.simpleType.list.directSubtypes")';
		}
	</TEMPLATE_CALL>
	<TEMPLATE_CALL>
		DESCR='Usage Locations'
		COND='getBooleanParam("doc.simpleType.list.usage")'
		TEMPLATE_FILE='typeUsage.tpl'
	</TEMPLATE_CALL>
	<FOLDER>
		DESCR='Annotation'
		COND='getBooleanParam("doc.simpleType.annotation")'
		COLLAPSED
		<BODY>
			<TEMPLATE_CALL>
				TEMPLATE_FILE='../ann/annotation.tpl'
			</TEMPLATE_CALL>
		</BODY>
		<HEADER>
			<AREA_SEC>
				FMT={
					par.style='s2';
				}
				<AREA>
					<CTRL_GROUP>
						FMT={
							trow.bkgr.color='#CCCCFF';
						}
						<CTRLS>
							<LABEL>
								TEXT='Annotation'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
	</FOLDER>
	<FOLDER>
		DESCR='TYPE DEFINITION DETAIL'
		COND='getBooleanParam("doc.simpleType.typeDef")'
		COLLAPSED
		<BODY>
			<AREA_SEC>
				FMT={
					sec.outputStyle='table';
					sec.spacing.before='10';
					sec.spacing.after='10';
					table.sizing='Relative';
					table.autofit='false';
					table.cellpadding.both='5';
					table.bkgr.color='#F5F5F5';
					table.border.style='solid';
					table.border.color='#999999';
					table.page.keepTogether='true';
					table.option.borderStylesInHTML='true';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<TEMPLATE_CALL_CTRL>
								FMT={
									ctrl.size.width='499.5';
									ctrl.size.height='17.3';
								}
								TEMPLATE_FILE='derivationTree.tpl'
							</TEMPLATE_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<TEMPLATE_CALL>
				TEMPLATE_FILE='../content/simpleContentDef.tpl'
			</TEMPLATE_CALL>
		</BODY>
		<HEADER>
			<AREA_SEC>
				FMT={
					par.style='s1';
				}
				<AREA>
					<CTRL_GROUP>
						FMT={
							trow.bkgr.color='#CCCCFF';
						}
						<CTRLS>
							<LABEL>
								TEXT='Type Definition Detail'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
	</FOLDER>
	<FOLDER>
		DESCR='XML SOURCE'
		COND='getBooleanParam("doc.simpleType.xmlSource")'
		<HTARGET>
			HKEYS={
				'contextElement.id';
				'"xml-source"';
			}
		</HTARGET>
		COLLAPSED
		<BODY>
			<AREA_SEC>
				COND='getBooleanParam("sec.xmlSource.box.component")'
				FMT={
					sec.outputStyle='table';
					table.sizing='Relative';
					table.autofit='false';
					table.cellpadding.both='5';
					table.bkgr.color='#F5F5F5';
					table.border.style='solid';
					table.border.color='#999999';
					table.option.borderStylesInHTML='true';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<TEMPLATE_CALL_CTRL>
								FMT={
									ctrl.size.width='499.5';
									ctrl.size.height='17.3';
								}
								TEMPLATE_FILE='../xml/nodeSource.tpl'
								PASSED_PARAMS={
									'remove.annotations','getBooleanParam("sec.xmlSource.remove.ann.component")';
								}
							</TEMPLATE_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<TEMPLATE_CALL>
				COND='! getBooleanParam("sec.xmlSource.box.component")'
				TEMPLATE_FILE='../xml/nodeSource.tpl'
				PASSED_PARAMS={
					'remove.annotations','getBooleanParam("sec.xmlSource.remove.ann.component")';
				}
			</TEMPLATE_CALL>
		</BODY>
		<HEADER>
			<AREA_SEC>
				FMT={
					par.style='s1';
				}
				<AREA>
					<CTRL_GROUP>
						FMT={
							trow.bkgr.color='#CCCCFF';
						}
						<CTRLS>
							<LABEL>
								TEXT='XML Source'
							</LABEL>
							<DELIMITER>
							</DELIMITER>
							<TEMPLATE_CALL_CTRL>
								FMT={
									text.style='cs6';
								}
								TEMPLATE_FILE='../xml/sourceNote.tpl'
								PASSED_PARAMS={
									'remove.annotations','getBooleanParam("sec.xmlSource.remove.ann.component")';
								}
							</TEMPLATE_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
	</FOLDER>
	<TEMPLATE_CALL>
		DESCR='Bottom Message'
		COND='output.type == "document"'
		TEMPLATE_FILE='../about.tpl'
	</TEMPLATE_CALL>
</ROOT>
<STOCK_SECTIONS>
	<FOLDER>
		SS_NAME='Datatype Representation'
		<BODY>
			<TEMPLATE_CALL>
				FMT={
					text.style='cs1';
				}
				TEMPLATE_FILE='../content/simpleContentRep.tpl'
			</TEMPLATE_CALL>
		</BODY>
		<HEADER>
			<AREA_SEC>
				FMT={
					par.style='s4';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='Datatype Representation Summary'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
	</FOLDER>
</STOCK_SECTIONS>
CHECKSUM='1mf15Y6TIRcLwleA0sjoLQ'
</DOCFLEX_TEMPLATE>
