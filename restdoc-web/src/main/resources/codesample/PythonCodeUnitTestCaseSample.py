#!/usr/bin/python
# -*- coding: UTF-8 -*-

from requests import request

if __name__ == '__main__':

       method = "${method}"
       headers = {}

       #if( !$null.isNull($requestHeaders) )
       #foreach($item in $requestHeaders )
       headers['${item.field}'] = "${item.value}"
       #end
       #end

       json = "${json}"
       #if( !$null.isNull($uriFormatStr) )
       url = "${url}".format(${uriFormatStr})
       #else
       url = ${url}
       #end

       response = request(method=method, url=url, json=json, headers=headers)
       print("status code : " + response.status_code)
       print("response headers : " + response.headers)
       print("response content ：" + response.content)