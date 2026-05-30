import {
	IAuthenticateGeneric,
	ICredentialTestRequest,
	ICredentialType,
	INodeProperties,
} from 'n8n-workflow';

export class QoNFerenCeRApi implements ICredentialType {
	name = 'qonferencerApi';
	displayName = 'QoNFerenCeR API';
	documentationUrl = 'https://github.com/RomanPruzinsky/QoNFerenCeR';

	properties: INodeProperties[] = [
		{
			displayName: 'Base URL',
			name: 'baseUrl',
			type: 'string',
			default: 'http://backend:8080',
			placeholder: 'http://backend:8080',
			description: 'Base URL of the QoNFerenCeR backend (internal Docker hostname recommended).',
		},
		{
			displayName: 'Service Token',
			name: 'apiToken',
			type: 'string',
			typeOptions: { password: true },
			default: '',
			description:
				'Service token issued by QoNFerenCeR admin. Used to authenticate internal endpoints.',
		},
	];

	authenticate: IAuthenticateGeneric = {
		type: 'generic',
		properties: {
			headers: {
				Authorization: '=Bearer {{$credentials.apiToken}}',
			},
		},
	};

	test: ICredentialTestRequest = {
		request: {
			baseURL: '={{$credentials.baseUrl}}',
			url: '/api/v1/internal/n8n/health',
			method: 'GET',
		},
	};
}
