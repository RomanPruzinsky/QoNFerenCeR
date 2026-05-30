import {
	IExecuteFunctions,
	INodeExecutionData,
	INodeType,
	INodeTypeDescription,
} from 'n8n-workflow';

export class QoNFerenCeR implements INodeType {
	description: INodeTypeDescription = {
		displayName: 'QoNFerenCeR',
		name: 'qoNFerenCeR',
		icon: 'file:qonferencer.svg',
		group: ['transform'],
		version: 1,
		subtitle: '={{ $parameter["operation"] }}',
		description: 'Interact with the QoNFerenCeR backend',
		defaults: {
			name: 'QoNFerenCeR',
		},
		inputs: ['main'],
		outputs: ['main'],
		credentials: [
			{
				name: 'qonferencerApi',
				required: true,
			},
		],
		properties: [
			{
				displayName: 'Operation',
				name: 'operation',
				type: 'options',
				noDataExpression: true,
				options: [
					{
						name: 'Health Check',
						value: 'healthCheck',
						description: 'Check backend health',
						action: 'Check backend health',
					},
				],
				default: 'healthCheck',
			},
		],
	};

	async execute(this: IExecuteFunctions): Promise<INodeExecutionData[][]> {
		const items = this.getInputData();
		const returnData: INodeExecutionData[] = [];

		for (let i = 0; i < items.length; i++) {
			returnData.push({ json: { ok: true } });
		}

		return [returnData];
	}
}
