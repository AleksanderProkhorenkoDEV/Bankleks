import { customElement, property, query, state } from 'lit/decorators.js';
import { LitElement, html } from 'lit';
import * as echarts from 'echarts';
import type { ECharts } from 'echarts';
import { donutStyle } from './donut.style';

@customElement('donut-chart')
export class DonutChart extends LitElement {

    @property({ type: Number }) income: number = 0;
    @property({ type: Number }) expense: number = 0;
    @state() _resizeObserver: ResizeObserver | null = null;


    @query('.chart-container') _container!: HTMLDivElement;

    private _chart: ECharts | null = null;

    static styles = [
        donutStyle,
    ]

    firstUpdated() {
        this._chart = echarts.init(this._container);
        this._setOptions();

        this._resizeObserver = new ResizeObserver(() => {
            this._chart?.resize();
        });
        this._resizeObserver.observe(this._container);
    }

    updated(changed: Map<string, unknown>) {
        if ((changed.has('income') || changed.has('expense')) && this._chart) {
            this._setOptions();
        }
    }

    disconnectedCallback() {
        super.disconnectedCallback();
        this._resizeObserver?.disconnect();
        this._chart?.dispose();
        this._chart = null;
    }

    private _setOptions() {
        const balance = this.income - this.expense;
        const balanceColor = balance >= 0 ? '#0F766E' : '#A50024';

        this._chart?.setOption({
            tooltip: {
                trigger: 'item',
                formatter: (params: any) => `${params.name}: ${params.value.toFixed(2)}€`
            },
            graphic: [{
                type: 'text',
                left: 'center',
                top: '38%',
                style: {
                    text: 'Balance',
                    fontSize: 11,
                    fontWeight: '600',
                    fill: '#134E4A',
                    fontFamily: 'Montserrat'
                }
            }, {
                type: 'text',
                left: 'center',
                top: '48%',
                style: {
                    text: `${balance >= 0 ? '+' : ''}${balance.toFixed(2)}€`,
                    fontSize: 16,
                    fontWeight: '800',
                    fill: balanceColor,
                    fontFamily: 'Montserrat'
                }
            }],
            series: [{
                type: 'pie',
                radius: ['58%', '78%'],
                avoidLabelOverlap: false,
                label: { show: false },
                emphasis: {
                    scale: true,
                    scaleSize: 6,
                },
                itemStyle: {
                    borderRadius: 6,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                data: [
                    { value: this.income, name: 'Ingresos', itemStyle: { color: '#0F766E' } },
                    { value: this.expense, name: 'Gastos', itemStyle: { color: '#A50024' } },
                ]
            }]
        });
    }

    render() {
        return html`
            <div class="wrapper">
                <div class="chart-container"></div>
                <div class="legend">
                    <div class="legend-item">
                        <div class="legend-dot" style="background:#0F766E"></div>
                        <div>
                            <div>Ingresos</div>
                            <div class="legend-value" style="color:#0F766E">+${this.income.toFixed(2)}€</div>
                        </div>
                    </div>
                    <div class="legend-item">
                        <div class="legend-dot" style="background:#A50024"></div>
                        <div>
                            <div>Gastos</div>
                            <div class="legend-value" style="color:#A50024">-${this.expense.toFixed(2)}€</div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
}

declare global {
    interface HTMLElementTagNameMap {
        "donut-chart": DonutChart
    }
}