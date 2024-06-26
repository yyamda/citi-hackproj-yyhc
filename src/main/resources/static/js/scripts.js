
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchButton').addEventListener('click', function() {
        var inputText = document.getElementById('stock-value').value;
        console.log(inputText);
    });
});