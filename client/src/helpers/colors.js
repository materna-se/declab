export default {
    colorIndex: 0,

    colors: [
        "#F0A3FF",
        "#2BCE48",
        "#FFCC99",
        "#808080",
        "#94FFB5",
        "#8F7C00",
        "#9DCC00",
        "#C20088",
        "#FFA405",
        "#FFA8BB",
        "#426600",
        "#FF0010",
        "#5EF1F2",
        "#00998F",
        "#E0FF66",
        "#740AFF",
        "#FFFF00",
        "#FF5005"
    ],

    distinguishableColor() {
        this.colorIndex = (this.colorIndex + 1) % this.colors.length;

        return this.colors[this.colorIndex];
    },
}