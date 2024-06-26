let polygonStockApiUrl = ["https://api.polygon.io/v2/aggs/ticker/", "/range/1/day/", "/", "?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"]
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchButton').addEventListener('click', function() {
        var stockName = document.getElementById('stock-value').value;
        var startDate = document.getElementById('start-date').value;
        var endDate = document.getElementById('end-date').value;

        console.log(stockName)
        let returnData = fetchData(stockName, startDate, endDate);
        console.log("API CALL completed", returnData);

        
    });
});


//        use this input Text: and the ap
//         api/{stock Name}/{private_api_key}
//      response.json()
//      send this over to the Java Controller

function fetchData(stock_name, start_date, end_date) {
//"https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2022-10-10/2023-01-09?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"
    let updatedUrl = polygonStockApiUrl[0] + stock_name + polygonStockApiUrl[1] + start_date + polygonStockApiUrl[2] + end_date + polygonStockApiUrl[3];
    console.log(updatedUrl);
    fetch(updatedUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.statusText);
            }
            return response.json();  // Parse JSON data from the response
        })
        .then(data => {
            return data // Handle the data from the API
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation: ', error);
        });
}