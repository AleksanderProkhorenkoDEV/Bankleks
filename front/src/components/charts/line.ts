import { customElement, property, query, state } from 'lit/decorators.js';
import type { BalancePoint } from '../../types/account';
import { lineStyle } from './line.style';
import type { ECharts } from 'echarts';
import { LitElement, html } from 'lit';
import * as echarts from 'echarts';

@customElement('line-chart')
export class LineChart extends LitElement {

    @property({ type: Array }) points: BalancePoint[] = [];
    @state() _resizeObserver: ResizeObserver | null = null;


    @query('.chart-container') _container!: HTMLDivElement;

    private _chart: ECharts | null = null;

    static styles = [
        lineStyle,
    ]

    firstUpdated() {
        if (this.points.length >= 2) {
            this._chart = echarts.init(this._container);
            this._setOptions();

            this._resizeObserver = new ResizeObserver(() => {
                this._chart?.resize();
            });
            this._resizeObserver.observe(this._container);
        }
    }

    updated(changed: Map<string, unknown>) {
        if (changed.has('points') && this.points.length >= 2) {
            if (!this._chart) {
                this._chart = echarts.init(this._container);
            }
            this._setOptions();
        }
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this._resizeObserver?.disconnect();
        this._chart?.dispose();
        this._chart = null;
    }

    private _formatDate(dateStr: string): string {
        const [y, m, d] = dateStr.split('-').map(Number);
        return new Date(y, m - 1, d).toLocaleDateString('es-ES', { day: '2-digit', month: 'short' });
    }

    private _setOptions() {
        const labels = this.points.map(p => this._formatDate(p.date));
        const balances = this.points.map(p => p.balance);

        this._chart?.setOption({
            tooltip: {
                trigger: 'axis',
                formatter: (params: any) => {
                    const p = params[0];
                    return `${p.axisValue}<br/><b>${p.value.toFixed(2)}€</b>`;
                },
                backgroundColor: '#fff',
                borderColor: '#E0FDF4',
                textStyle: { color: '#1F2937', fontFamily: 'Montserrat' }
            },
            grid: { left: 16, right: 16, top: 20, bottom: 30, containLabel: true },
            xAxis: {
                type: 'category',
                data: labels,
                axisLine: { show: false },
                axisTick: { show: false },
                axisLabel: {
                    color: '#134E4A',
                    fontFamily: 'Montserrat',
                    fontSize: 10,
                    interval: Math.ceil(labels.length / 5)
                }
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: (val: number) => `${val}€`,
                    color: '#134E4A',
                    fontFamily: 'Montserrat',
                    fontSize: 10
                },
                splitLine: { lineStyle: { color: 'rgba(19,78,74,0.08)' } }
            },
            series: [{
                type: 'line',
                data: balances,
                smooth: true,
                symbol: 'circle',
                symbolSize: 6,
                lineStyle: { color: '#0F766E', width: 2.5 },
                itemStyle: {
                    color: '#0F766E',
                    borderColor: '#fff',
                    borderWidth: 2
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(15,118,110,0.25)' },
                        { offset: 1, color: 'rgba(15,118,110,0)' }
                    ])
                }
            }]
        });
    }

    render() {
        if (this.points.length < 2) {
            return html`<div class="empty">Sin datos suficientes</div>`;
        }
        return html`<div class="chart-container"></div>`;
    }
}

declare global {
    interface HTMLElementTagNameMap { 'line-chart': LineChart }
}