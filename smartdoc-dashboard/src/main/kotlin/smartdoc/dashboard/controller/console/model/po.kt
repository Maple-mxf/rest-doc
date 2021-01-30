package smartdoc.dashboard.controller.console.model

import smartdoc.dashboard.model.doc.DocType

internal data class DocPojo(val _id: String, val resource: String,
                            val docType: DocType, val name: String,
                            val order: Int = 0, val createTime: Long = -1L)