<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-09-25 12:34:00'
LAST_UPDATE='2007-09-08 09:40:54'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ET='#DOCUMENTS'
<HTARGET>
	HKEYS={
		'"xmlns-bindings"';
	}
</HTARGET>
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
		style.name='Main Heading';
		style.id='s1';
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
	}
	CHAR_STYLE={
		style.name='Markup';
		style.id='cs5';
		text.color.foreground='#0000FF';
	}
	CHAR_STYLE={
		style.name='Name';
		style.id='cs6';
		text.color.foreground='#990000';
	}
	PAR_STYLE={
		style.name='Normal';
		style.id='s2';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Page header / footer';
		style.id='cs7';
		text.font.name='Arial';
		text.font.style.italic='true';
	}
	CHAR_STYLE={
		style.name='Page Number';
		style.id='cs8';
		text.font.size='9';
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
		style.id='s3';
		text.font.size='8';
		text.font.style.bold='true';
		par.lineHeight='11';
		par.margin.right='7';
	}
	PAR_STYLE={
		style.name='Property Value';
		style.id='s4';
		text.font.name='Verdana';
		text.font.size='8';
		par.lineHeight='11';
	}
	CHAR_STYLE={
		style.name='Summary Heading Font';
		style.id='cs10';
		text.font.size='12';
		text.font.style.bold='true';
	}
	CHAR_STYLE={
		style.name='XML Source';
		style.id='cs11';
		text.font.name='Verdana';
		text.font.size='8';
	}
</STYLES>
<PAGE_FOOTER>
	<AREA_SEC>
		FMT={
			sec.outputStyle='table';
			text.style='cs7';
			table.sizing='Relative';
			table.cellpadding.horz='1';
			table.border.style='none';
			table.border.top.style='solid';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						FMT={
							ctrl.size.width='305.3';
							ctrl.size.height='39.8';
						}
						TEXT='Namespace Bindings'
					</LABEL>
					<PANEL>
						FMT={
							content.outputStyle='text-par';
							ctrl.size.width='194.3';
							ctrl.size.height='39.8';
							tcell.align.horz='Right';
							table.border.style='none';
						}
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='Page'
									</LABEL>
									<DATA_CTRL>
										DOCFIELD='page'
									</DATA_CTRL>
									<LABEL>
										TEXT='of'
									</LABEL>
									<DATA_CTRL>
										DOCFIELD='num-pages'
									</DATA_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</PANEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
</PAGE_FOOTER>
<ROOT>
	<AREA_SEC>
		DESCR='XML\'s doc heading'
		FMT={
			par.style='s1';
			par.page.keepWithNext='true';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						TEXT='Namespace Bindings'
					</LABEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<ELEMENT_ITER>
		FMT={
			sec.outputStyle='table';
			sec.spacing.before='14';
			table.sizing='Relative';
			table.cellpadding.both='3';
		}
		TARGET_ET='#NAMESPACE'
		SCOPE='advanced-location-rules'
		RULES={
			'* -> #DOCUMENT/namespaces^::#NAMESPACE';
		}
		SORTING='by-compound-key'
		SORTING_KEY={
			{lpath='@prefix',ascending};
			{lpath='@namespaceURI',ascending};
			{expr='getXMLDocument().getAttrStringValue("xmlName")',ascending};
		}
		<BODY>
			<AREA_SEC>
				COND='getAttrValue("prefix") == ""'
				FMT={
					trow.align.vert='Top';
					trow.page.keepTogether='true';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='44.3';
									ctrl.size.height='17.3';
									text.style='cs1';
								}
								TEXT='-'
							</LABEL>
							<SS_CALL_CTRL>
								FMT={
									ctrl.size.width='423.8';
									ctrl.size.height='17.3';
									tcell.sizing='Relative';
								}
								SS_NAME='xmlns'
							</SS_CALL_CTRL>
							<DATA_CTRL>
								COND='output.format.supportsPagination'
								FMT={
									ctrl.size.width='31.5';
									ctrl.size.height='17.3';
									ctrl.option.noHLinkFmt='true';
									tcell.align.horz='Center';
									text.style='cs8';
									text.font.style.italic='true';
									text.hlink.fmt='none';
								}
								<DOC_HLINK>
									HKEYS={
										'getAttrValue("elementId")';
										'"xmlns"';
									}
								</DOC_HLINK>
								DOCFIELD='page-htarget'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='getAttrValue("prefix") != ""'
				FMT={
					trow.align.vert='Top';
					trow.page.keepTogether='true';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<DATA_CTRL>
								FMT={
									ctrl.size.width='44.3';
									ctrl.size.height='17.3';
									text.style='cs1';
								}
								ATTR='prefix'
							</DATA_CTRL>
							<SS_CALL_CTRL>
								FMT={
									ctrl.size.width='424.5';
									ctrl.size.height='17.3';
									tcell.sizing='Relative';
								}
								SS_NAME='xmlns'
							</SS_CALL_CTRL>
							<DATA_CTRL>
								COND='output.format.supportsPagination'
								FMT={
									ctrl.size.width='30.8';
									ctrl.size.height='17.3';
									ctrl.option.noHLinkFmt='true';
									tcell.align.horz='Center';
									text.style='cs8';
									text.font.style.italic='true';
									text.hlink.fmt='none';
								}
								<DOC_HLINK>
									HKEYS={
										'getAttrValue("elementId")';
										'"xmlns"';
									}
								</DOC_HLINK>
								DOCFIELD='page-htarget'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
		<HEADER>
			<AREA_SEC>
				FMT={
					text.font.style.bold='true';
					trow.bkgr.color='#CCCCFF';
					trow.page.keepTogether='true';
					trow.page.keepWithNext='true';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='45';
									ctrl.size.height='17.3';
								}
								TEXT='Prefix'
							</LABEL>
							<LABEL>
								FMT={
									ctrl.size.width='423';
									ctrl.size.height='17.3';
									tcell.sizing='Relative';
								}
								TEXT='Namespace URI / Binding Location'
							</LABEL>
							<LABEL>
								COND='output.format.supportsPagination'
								FMT={
									ctrl.size.width='31.5';
									ctrl.size.height='17.3';
									tcell.align.horz='Center';
									text.style='cs8';
									text.font.style.bold='true';
									text.font.style.italic='true';
								}
								TEXT='Page'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
	</ELEMENT_ITER>
	<TEMPLATE_CALL>
		DESCR='Bottom Message'
		COND='output.type == "document"'
		TEMPLATE_FILE='../about.tpl'
	</TEMPLATE_CALL>
</ROOT>
<STOCK_SECTIONS>
	<FOLDER>
		MATCHING_ET='#NAMESPACE'
		FMT={
			text.font.size='8';
		}
		SS_NAME='xmlns'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						FMT={
							par.margin.bottom='6';
						}
						<CTRLS>
							<DATA_CTRL>
								FMT={
									text.style='cs2';
								}
								<URL_HLINK>
									TARGET_FRAME_EXPR='"_blank"'
									TARGET_FRAME_ALWAYS
									URL_EXPR='getAttrStringValue("namespaceURI")'
								</URL_HLINK>
								ATTR='namespaceURI'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				FMT={
					sec.outputStyle='table';
					table.cellpadding.both='0';
					table.border.style='none';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='39';
									ctrl.size.height='17.3';
									par.style='s3';
								}
								TEXT='File:'
							</LABEL>
							<DATA_CTRL>
								CONTEXT_ELEMENT_EXPR='getXMLDocument()'
								MATCHING_ET='#DOCUMENT'
								FMT={
									ctrl.size.width='460.5';
									ctrl.size.height='17.3';
									text.style='cs9';
								}
								<DOC_HLINK>
									HKEYS={
										'getXMLDocument().id';
										'"detail"';
									}
								</DOC_HLINK>
								ATTR='xmlName'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='39';
									ctrl.size.height='38.3';
									par.style='s3';
								}
								TEXT='Element:'
							</LABEL>
							<PANEL>
								FMT={
									content.outputStyle='text-par';
									ctrl.size.width='460.5';
									ctrl.size.height='38.3';
									text.style='cs11';
									text.decor.underline='false';
									text.hlink.fmt='none';
								}
								<DOC_HLINK>
									HKEYS={
										'getAttrValue("elementId")';
										'"xmlns"';
									}
								</DOC_HLINK>
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<LABEL>
												FMT={
													text.style='cs5';
												}
												TEXT='<'
											</LABEL>
											<DELIMITER>
												FMT={
													txtfl.delimiter.type='none';
												}
											</DELIMITER>
											<DATA_CTRL>
												FMT={
													text.style='cs6';
												}
												FORMULA='getElementByLinkAttr("elementId").dsmElement.qName'
											</DATA_CTRL>
											<LABEL>
												FMT={
													text.style='cs6';
												}
												TEXT='...'
											</LABEL>
											<LABEL>
												FMT={
													text.style='cs5';
												}
												TEXT='>'
											</LABEL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
</STOCK_SECTIONS>
CHECKSUM='5CKdsLBGs8gZxEHUrLWgqA'
</DOCFLEX_TEMPLATE>
