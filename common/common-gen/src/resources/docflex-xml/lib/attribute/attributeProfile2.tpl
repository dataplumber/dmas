<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-10-13 03:37:00'
LAST_UPDATE='2007-09-08 09:40:53'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ET='xs:attribute'
<TEMPLATE_PARAMS>
	PARAM={
		param.name='nsURI';
		param.displayName='attribute\'s namespace URI';
		param.description='The namespace to which this global attribute belongs';
		param.type='string';
	}
	PARAM={
		param.name='usageCount';
		param.description='number of locations where this global attribute is used';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "attribute-usage",\n  QName (getStringParam("nsURI"), getAttrStringValue("name"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='showNS';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='showSchema';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='page.refs';
		param.displayName='Generate page references';
		param.type='boolean';
		param.boolean.default='true';
	}
</TEMPLATE_PARAMS>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs4';
}
<STYLES>
	CHAR_STYLE={
		style.name='Code';
		style.id='cs1';
		text.font.name='Courier New';
		text.font.size='9';
	}
	CHAR_STYLE={
		style.name='Code Smaller';
		style.id='cs2';
		text.font.name='Courier New';
		text.font.size='8';
	}
	CHAR_STYLE={
		style.name='Default Paragraph Font';
		style.id='cs3';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Hyperlink';
		style.id='cs4';
		text.decor.underline='true';
		text.color.foreground='#0000FF';
	}
	PAR_STYLE={
		style.name='Normal';
		style.id='s1';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Page Number Small';
		style.id='cs5';
		text.font.name='Courier New';
		text.font.size='8';
	}
	CHAR_STYLE={
		style.name='Property Title Font';
		style.id='cs6';
		text.font.size='8';
		text.font.style.bold='true';
		par.lineHeight='11';
		par.margin.right='7';
	}
	CHAR_STYLE={
		style.name='Property Value Font';
		style.id='cs7';
		text.font.name='Verdana';
		text.font.size='8';
		par.lineHeight='11';
	}
</STYLES>
<ROOT>
	<AREA_SEC>
		COND='getBooleanParam("showNS")'
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							text.style='cs6';
						}
						TEXT='Namespace:'
					</LABEL>
					<DELIMITER>
						FMT={
							text.style='cs1';
						}
					</DELIMITER>
					<DATA_CTRL>
						FMT={
							text.style='cs2';
						}
						<DOC_HLINK>
							HKEYS={
								'getStringParam("nsURI")';
								'"detail"';
							}
						</DOC_HLINK>
						<URL_HLINK>
							COND='getStringParam("nsURI") != ""'
							ALT_HLINK
							TARGET_FRAME_EXPR='"_blank"'
							URL_EXPR='getStringParam("nsURI")'
						</URL_HLINK>
						FORMULA='(ns = getParam("nsURI")) != "" ? ns : "{global namespace}"'
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							text.style='cs6';
						}
						TEXT='Type:'
					</LABEL>
					<DELIMITER>
						FMT={
							text.style='cs1';
						}
					</DELIMITER>
					<SS_CALL_CTRL>
						FMT={
							text.style='cs7';
						}
						SS_NAME='Type Info'
					</SS_CALL_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		COND='getBooleanParam("showSchema")'
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							text.style='cs6';
						}
						TEXT='Defined:'
					</LABEL>
					<DELIMITER>
						FMT={
							text.style='cs1';
						}
					</DELIMITER>
					<PANEL>
						FMT={
							ctrl.size.width='314.3';
							ctrl.size.height='98.3';
							text.style='cs7';
						}
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='globally in'
									</LABEL>
									<DATA_CTRL>
										<DOC_HLINK>
											HKEYS={
												'getXMLDocument().id';
												'"detail"';
											}
										</DOC_HLINK>
										FORMULA='getXMLDocument().getAttrStringValue("xmlName")'
									</DATA_CTRL>
									<PANEL>
										COND='hyperTargetExists (Array (contextElement.id, "xml-source"))'
										FMT={
											ctrl.size.width='288.8';
											ctrl.size.height='59.3';
										}
										<AREA>
											<CTRL_GROUP>
												<CTRLS>
													<DELIMITER>
														FMT={
															txtfl.delimiter.type='text';
															txtfl.delimiter.text=', ';
														}
													</DELIMITER>
													<LABEL>
														TEXT='see'
													</LABEL>
													<LABEL>
														<DOC_HLINK>
															HKEYS={
																'contextElement.id';
																'"xml-source"';
															}
														</DOC_HLINK>
														TEXT='XML source'
													</LABEL>
													<PANEL>
														COND='output.format.supportsPagination && \ngetBooleanParam("page.refs")'
														FMT={
															ctrl.size.width='186';
															ctrl.size.height='38.3';
															txtfl.delimiter.type='none';
														}
														<AREA>
															<CTRL_GROUP>
																<CTRLS>
																	<DELIMITER>
																		FMT={
																			txtfl.delimiter.type='nbsp';
																		}
																	</DELIMITER>
																	<LABEL>
																		FMT={
																			text.style='cs5';
																		}
																		TEXT='['
																	</LABEL>
																	<DATA_CTRL>
																		FMT={
																			ctrl.option.noHLinkFmt='true';
																			text.style='cs5';
																			text.hlink.fmt='none';
																		}
																		<DOC_HLINK>
																			HKEYS={
																				'contextElement.id';
																				'"xml-source"';
																			}
																		</DOC_HLINK>
																		DOCFIELD='page-htarget'
																	</DATA_CTRL>
																	<LABEL>
																		FMT={
																			text.style='cs5';
																		}
																		TEXT=']'
																	</LABEL>
																</CTRLS>
															</CTRL_GROUP>
														</AREA>
													</PANEL>
												</CTRLS>
											</CTRL_GROUP>
										</AREA>
									</PANEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</PANEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		COND='getIntParam("usageCount") == 0'
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							text.style='cs6';
						}
						TEXT='Used:'
					</LABEL>
					<DELIMITER>
						FMT={
							text.style='cs1';
						}
					</DELIMITER>
					<LABEL>
						FMT={
							text.style='cs7';
						}
						TEXT='never'
					</LABEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		COND='getIntParam("usageCount") > 0'
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							text.style='cs6';
						}
						TEXT='Used:'
					</LABEL>
					<DELIMITER>
						FMT={
							text.style='cs1';
						}
					</DELIMITER>
					<PANEL>
						FMT={
							ctrl.size.width='210';
							text.style='cs7';
						}
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='at'
									</LABEL>
									<DATA_CTRL>
										FORMULA='getIntParam("usageCount")'
									</DATA_CTRL>
									<LABEL>
										COND='getIntParam("usageCount") == 1'
										<DOC_HLINK>
											HKEYS={
												'contextElement.id';
												'"usage-locations"';
											}
										</DOC_HLINK>
										TEXT='location'
									</LABEL>
									<LABEL>
										COND='getIntParam("usageCount") > 1'
										<DOC_HLINK>
											HKEYS={
												'contextElement.id';
												'"usage-locations"';
											}
										</DOC_HLINK>
										TEXT='locations'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</PANEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
</ROOT>
<STOCK_SECTIONS>
	<FOLDER>
		MATCHING_ET='xs:attribute'
		FMT={
			sec.outputStyle='text-par';
		}
		SS_NAME='Type Info'
		<BODY>
			<AREA_SEC>
				COND='getAttrStringValue("type") == ""'
				CONTEXT_ELEMENT_EXPR='findChild("xs:complexType | xs:simpleType")'
				MATCHING_ETS={'xs:%complexType';'xs:%simpleType'}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"detail"';
									}
								</DOC_HLINK>
								TEXT='embedded'
							</LABEL>
							<TEMPLATE_CALL_CTRL>
								TEMPLATE_FILE='../type/derivationSummary.tpl'
								ALT_FORMULA='"simpleType"'
							</TEMPLATE_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<FOLDER>
				DESCR='when there\'s a reference to a global type'
				COND='getAttrStringValue("type") != ""'
				<BODY>
					<AREA_SEC>
						DESCR='when the reference is resolved into a documented element'
						CONTEXT_ELEMENT_EXPR='findElementByKey ("types", getAttrQNameValue("type"))'
						MATCHING_ETS={'xs:complexType';'xs:simpleType'}
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<DATA_CTRL>
										FMT={
											text.style='cs2';
										}
										<DOC_HLINK>
											HKEYS={
												'contextElement.id';
												'"detail"';
											}
										</DOC_HLINK>
										FORMULA='name = getAttrStringValue("name");\n\nschema = getXMLDocument().findChild ("xs:schema");\nnsURI = schema.getAttrStringValue("targetNamespace");\n\nQName (nsURI, name, Enum (rootElement, contextElement))'
									</DATA_CTRL>
									<PANEL>
										COND='output.format.supportsPagination &&\ngetBooleanParam("page.refs") &&\nhyperTargetExists (Array (contextElement.id,  "detail"))'
										FMT={
											ctrl.size.width='154.5';
											text.style='cs5';
											txtfl.delimiter.type='none';
										}
										<AREA>
											<CTRL_GROUP>
												<CTRLS>
													<LABEL>
														TEXT='['
													</LABEL>
													<DATA_CTRL>
														FMT={
															ctrl.option.noHLinkFmt='true';
															text.hlink.fmt='none';
														}
														<DOC_HLINK>
															HKEYS={
																'contextElement.id';
																'"detail"';
															}
														</DOC_HLINK>
														DOCFIELD='page-htarget'
													</DATA_CTRL>
													<LABEL>
														TEXT=']'
													</LABEL>
												</CTRLS>
											</CTRL_GROUP>
										</AREA>
									</PANEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
					<AREA_SEC>
						DESCR='otherwise, the referenced global type is not within documentation scope'
						COND='sectionBlock.execSecNone'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<DATA_CTRL>
										FMT={
											text.style='cs2';
										}
										ATTR='type'
									</DATA_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</BODY>
			</FOLDER>
			<AREA_SEC>
				DESCR='otherwise, if no type information specified, assume anySimpleType'
				COND='sectionBlock.execSecNone'
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									text.style='cs2';
								}
								TEXT='anySimpleType'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
</STOCK_SECTIONS>
CHECKSUM='1ezWRIAatDSc?RfsdSLf5A'
</DOCFLEX_TEMPLATE>
