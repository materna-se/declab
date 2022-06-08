export default {
    table: [],
    rowHeaders: [],
    columnHeaders: [],

    recWrapper(root, options) {
        this.rec(root, options, 0, this.table);
    },

    /**
     * This code will recursively extract a 1- or 2-dimensional slice (i.e. a table) 
     * from the discovery tree. It will take into account which selectors have been
     * pinned and navigate the tree such that the correctly-valued node for pinned
     * selectors is always chosen.
     * 
     * It doesn't directly return the results, instead it populates the variables table,
     * rowHeaders and columnHeaders.
     * @param {*} root      The root of the (sub-)tree
     * @param {*} options   Configured options from discoverer.vue, such as pinned selectors
     * @param {*} depth     The depth that this recursive algorithm has reached
     * @param {*} tableCell The cell of the table that this instance of the algorithm will 
     *                      place its result in
     */
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
            tableCell.push(root.output);
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
