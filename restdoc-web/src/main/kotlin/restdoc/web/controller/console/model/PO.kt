package restdoc.web.controller.console.model

import restdoc.web.model.doc.DocType

data class DocPojo(val _id: String, val resource: String, val docType: DocType, val name: String, val order: Int = 0, val createTime: Long = -1L)