/**
 *
 * @param array
 */
function getFirstDocument(array) {

    if (array == null || array.length === 0) return null;

    var length = array.length;


    var flag = 0;

    for (let i = 0; i < length; i++) {
        var node = array[i];
        if ("API" === node['type'] || "WIKI" === node['type']) {
            if (flag === 0) {
                return node['id']
            }
        } else {
            var result = findFirstChildren(node);
            if (result != null) return result;
        }
    }
    return null;
}

function findFirstChildren(parentNode) {
    if (parentNode != null && node['children'].length === 0) {
        return null;
    } else {
        var children = parentNode['children'];
        var length = children.length;

        for (let i = 0; i < length; i++) {
            if (children[i]['type'] === 'WIKI' || children[i]['type'] === 'API') {
                return children[i]['id']
            } else {
                var result = findFirstChildren(children[i]);
                if (null != result) {
                    return result;
                }
            }
        }
        return null;
    }
}