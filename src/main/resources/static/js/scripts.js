let polygonStockApiUrl = ["https://api.polygon.io/v2/aggs/ticker/", "/range/1/day/", "/", "?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"]

document.addEventListener("DOMContentLoaded", function() {

    const searchButton = document.querySelector(".search-button");
    searchButton.addEventListener("click", prepareData);

    async function prepareData() {
        var stockName = document.querySelector(".stock-value").value;
        var startDate = document.querySelector(".start-date").value;
        var endDate = document.querySelector(".end-date").value;

        console.log(stockName);
        console.log(startDate);
        console.log(endDate);

        try {
            let apiResponse = await fetchData(stockName, startDate, endDate);
            console.log("API CALL completed");
            console.log(apiResponse); // this is JSON format.

            let processResponse = await sendToServer(apiResponse, startDate, endDate);
            console.log("Server processing completed", processResponse);
        } catch (error) {
            console.error("Error fetching or sending data");
        }

        }

    async function fetchData(stock_name, start_date, end_date) {
    //"https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2022-10-10/2023-01-09?apiKey=5Jgi3YlL3jI6A3LAENnC1qqs7ebQz4HU"
        let updatedUrl = polygonStockApiUrl[0] + stock_name + polygonStockApiUrl[1] + start_date + polygonStockApiUrl[2] + end_date + polygonStockApiUrl[3];
        console.log(updatedUrl);

        const response = await fetch(updatedUrl);
        if (!response.ok) {
            throw new Error('Network response was not ok: ' + response.statusText);
        }
        return await response.json();
    }

    async function sendToServer(data, startDate, endDate) {

        const requestBody = {
              ...data,
              startDate: startDate,
              endDate: endDate
        };

        const response = await fetch('/processStockData', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.text();
    }
})
