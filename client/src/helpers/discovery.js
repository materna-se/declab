import isEqual from "lodash/isEqual"

export class Node {
    constructor(values, subtree) {
        this.values = values;
        this.subtree = subtree;
    }

    appendNode(node) {
        this.subtree.push(node);
    }

    subtreeEquals(other) {
        if (other instanceof Leaf) {
            return false;
        }
        return isEqual(this.subtree, other.subtree);
    }

    getFirst() {
        if (this.subtree.length > 0) {
            return this.subtree[0];
        } else {
            return undefined;
        }
    }

    getLast() {
        if (this.subtree.length > 0) {
            return this.subtree[this.subtree.length - 1];
        } else {
            return undefined;
        }
    }

    get(index) {
        return this.subtree[index];
    }

    merge(other) {
        var node = this.getLast();

        if (node === undefined) {
            this.appendNode(other);
            return;
        }

        if (node.subtreeEquals(other)) {
            node.values = node.values.concat(other.values);
        } else {
            this.appendNode(other);
        }
    }
}

export class Leaf {
    constructor(values, output) {
        this.values = values;
        this.output = output;
    }

    subtreeEquals(other) {
        if (other instanceof Leaf) {
            return JSON.stringify(this.output) === JSON.stringify(other.output);
        }
        return false;
    }
}