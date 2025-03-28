<template>
  <div class="visualization-container">
    <div class="control-panel">
      <el-form :inline="true">
        <el-form-item label="采样率">
          <el-select v-model="samplingRate" @change="loadData">
            <el-option label="1:1" :value="1" />
            <el-option label="1:10" :value="10" />
            <el-option label="1:100" :value="100" />
            <el-option label="1:1000" :value="1000" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示列">
          <el-select v-model="selectedColumns" multiple @change="updateChart">
            <el-option
              v-for="field in fields"
              :key="field"
              :label="field"
              :value="field"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="startReplay">
            开始回放
          </el-button>
          <el-button @click="pauseReplay" :disabled="!isPlaying">
            暂停
          </el-button>
        </el-form-item>
        <el-form-item label="回放速度">
          <el-slider
            v-model="replaySpeed"
            :min="1"
            :max="10"
            :step="1"
            show-stops
          />
        </el-form-item>
      </el-form>
    </div>

    <div ref="chartContainer" class="chart-container"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from 'axios'

export default {
  name: 'DataVisualization',
  props: {
    fileId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      chart: null,
      data: [],
      fields: [],
      selectedColumns: [],
      samplingRate: 1,
      replaySpeed: 1,
      isPlaying: false,
      currentIndex: 0,
      replayInterval: null
    }
  },
  async mounted() {
    this.initChart()
    await this.loadDataInfo()
    await this.loadData()
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.dispose()
    }
    this.stopReplay()
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.chartContainer)
      window.addEventListener('resize', this.handleResize)
    },
    handleResize() {
      this.chart && this.chart.resize()
    },
    async loadDataInfo() {
      try {
        const response = await axios.get(`/api/data/${this.fileId}/info`)
        this.fields = response.data.schema.fields.map(f => f.name)
        this.selectedColumns = this.fields.slice(0, 2) // 默认选择前两列
      } catch (error) {
        console.error('加载数据信息失败:', error)
      }
    },
    async loadData() {
      try {
        const response = await axios.get(`/api/data/${this.fileId}?samplingRate=${this.samplingRate}`)
        this.data = response.data.data.data
        this.updateChart()
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    updateChart() {
      if (!this.selectedColumns.length) return

      const series = this.selectedColumns.map(field => ({
        name: field,
        type: 'line',
        data: this.data.map(row => {
          const index = this.fields.indexOf(field)
          return row[index]
        })
      }))

      const option = {
        title: {
          text: '数据回放视图'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: this.selectedColumns
        },
        xAxis: {
          type: 'category',
          data: this.data.map((_, index) => index)
        },
        yAxis: {
          type: 'value'
        },
        series
      }

      this.chart.setOption(option)
    },
    startReplay() {
      if (this.isPlaying) return

      this.isPlaying = true
      this.currentIndex = 0
      
      this.replayInterval = setInterval(() => {
        if (this.currentIndex >= this.data.length) {
          this.stopReplay()
          return
        }

        const visibleData = this.data.slice(0, this.currentIndex + 1)
        const series = this.selectedColumns.map(field => ({
          name: field,
          type: 'line',
          data: visibleData.map(row => {
            const index = this.fields.indexOf(field)
            return row[index]
          })
        }))

        this.chart.setOption({
          series
        })

        this.currentIndex += this.replaySpeed
      }, 100)
    },
    pauseReplay() {
      this.isPlaying = false
      clearInterval(this.replayInterval)
    },
    stopReplay() {
      this.isPlaying = false
      clearInterval(this.replayInterval)
      this.currentIndex = 0
      this.updateChart()
    }
  }
}
</script>

<style scoped>
.visualization-container {
  padding: 20px;
}

.control-panel {
  margin-bottom: 20px;
}

.chart-container {
  width: 100%;
  height: 600px;
}
</style>