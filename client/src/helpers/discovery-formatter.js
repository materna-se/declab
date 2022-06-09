export default {
    table: [],
    rowHeaders: [],
    columnHeaders: [],

    recWrapper(root, options) {
        this.rec(root, options, 0, this.table);
    },

    rec(root, options, depth, tableCell) {
        // Reset globals
        if (depth == 0) {
            this.table = [];
            this.rowHeaders = [];
            this.columnHeaders = [];

            tableCell = this.table;
        }

        // Have we reached a leaf?
        if (root.subtree === undefined) {
            // Is this selector pinned?
            const leaf_selector = options.inputs[depth-1].selector;

            if (leaf_selector in options.pinnedSelectors && options.pinnedSelectors[leaf_selector] !== null) {
                tableCell.push(root.output);
            } else {
                    tableCell.push(root.output);
            }
            return;
        }

        const selector = options.inputs[depth].selector;

        // Is this selector pinned?
        if (selector in options.pinnedSelectors && options.pinnedSelectors[selector] !== null) {
            let pinnedNode = root.getByValue(options.pinnedSelectors[selector]);

            this.rec(pinnedNode, options, depth + 1, tableCell);
        } else {
            // Are there any other unpinned selectors further down the tree?
            var lastUnpinned = true;

            for (var otherSelector of options.inputs.slice(depth+1).map(function(input){return input.selector})) {
                if (options.pinnedSelectors[otherSelector] == null) {
                    lastUnpinned = false;
                }
            }

            const columnHeadersNeeded = this.columnHeaders.length == 0;

            for (var node of root.subtree) {
                if (lastUnpinned) {
                    // Only add column headers if it hasn't already been done
                    if (columnHeadersNeeded) {
                        for (const x of node.values) {
                            this.columnHeaders.push(x);
                        }
                    }

                    for (const x of node.values) {
                        this.rec(node, options, depth + 1, tableCell);
                    }
                } else {
                    this.rowHeaders.push(node.values);

                    let tableCellLower = [];
                    tableCell.push(tableCellLower);

                    this.rec(node, options, depth+1, tableCellLower);
                }
            }
        }
    }
}
