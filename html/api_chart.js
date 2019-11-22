var drawCreatedAPI = function(opts) {
    let options = {
        chart: {
            renderTo: opts.containerId,
    //        type: 'spline'
            type: 'line'
        },
    
        title: {
            text: opts.title,
        },
    
        xAxis: {
            type: 'category',
        },
    
        yAxis: {
            title: {
                text: null
            }
        },
    
        tooltip: {
            crosshairs: true,
            shared: true,
            valueSuffix: 'ms'
        },
    
        legend: {
        },
    
        series: [],
    };

    let dataArray = [];
    let getData = function(csvFile, dataName) {
        return new Promise(function(resolve, reject) {
            let tmpArray = [];
            $.get(csvFile, function(csv) {
                let content_lines = csv.split("\n");
                for (let i in content_lines) {
                    let lines = content_lines[i].split(",");
                    tmpArray.push([lines[0], parseFloat(lines[1])]);
                }
                tmpArray.pop();
    
                dataArray.push(tmpArray);
                resolve(tmpArray);
                options.series.push({data: tmpArray, name: dataName});
            });
        });
    }
    
    
    async function getAllData() {
        for (let e of opts.seriesData) {
            await getData(opts.prefixDir + e + '.csv', e);
        }
        let chart = new Highcharts.Chart(options);
        console.log(dataArray);
    }
    
    getAllData();
}

